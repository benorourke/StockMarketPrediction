package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.Framework;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.TimeSeriesManager;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.DataType;
import net.benorourke.stocks.framework.series.data.RawDataAnnotation;
import net.benorourke.stocks.framework.series.data.RawDataElementAnnotation;
import net.benorourke.stocks.framework.series.data.impl.Document;
import net.benorourke.stocks.framework.series.data.impl.StockQuote;
import net.benorourke.stocks.framework.util.*;
import net.benorourke.stocks.userinterface.scene.Controller;
import net.benorourke.stocks.userinterface.scene.SceneHelper;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import static net.benorourke.stocks.userinterface.StockApplication.runBgThread;
import static net.benorourke.stocks.userinterface.StockApplication.runUIThread;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.GENERIC_INPUT_FIELD_FXML;
import static net.benorourke.stocks.userinterface.scene.dashboard.DashboardController.GENERIC_INPUT_FIELD_WIDTH_BIND_COEFF;

public class InjectionPaneHandler extends PaneHandler
{
    private final JFXComboBox injectionSourceComboBox;
    private final JFXDatePicker injectionDatePicker;
    private final VBox injectionDataBox;
    private final JFXButton injectionInjectButton;

    private Map<DataSource, HashSet<RawDataField>> fieldCache;

    public InjectionPaneHandler(DashboardController controller, DashboardModel model,
                                JFXComboBox injectionSourceComboBox, JFXDatePicker injectionDatePicker,
                                VBox injectionDataBox, JFXButton injectionInjectButton)
    {
        super(controller, model);

        this.injectionSourceComboBox = injectionSourceComboBox;
        this.injectionDatePicker = injectionDatePicker;
        this.injectionDataBox = injectionDataBox;
        this.injectionInjectButton = injectionInjectButton;

        this.fieldCache = new LinkedHashMap<>();
    }

    @Override
    public void initialise()
    {
        model.acquireDataSources(() ->
        {
            List<DataSource> sources = model.getDataSources();

            // Populate all of the collection sources to choose from
            List<String> strSources = sources.stream()
                                             .map(s -> s.getName() + " (" + s.getDataType().getName() + ")")
                                             .collect(Collectors.toList());
            injectionSourceComboBox.getItems().addAll(strSources);
            // Change the current collection source to whatever they just selected
            injectionSourceComboBox.valueProperty().addListener((observable, oldValue, newValue) ->
                    selectInjectionDataSource(sources.get(strSources.indexOf(newValue))));

            // Select the first source - there's several inbuilt ones so this is safe
            injectionSourceComboBox.getSelectionModel().select(0);
        });

        injectionInjectButton.setOnMouseClicked(e -> onInjectClicked());
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series) { }

    private void selectInjectionDataSource(DataSource source)
    {
        if (model.getCurrentlySelectedInjectionDataSource() != null
                && model.getCurrentlySelectedInjectionDataSource().equals(source))
            return;

        model.setCurrentlySelectedInjectionDataSource(source);
        populateDataBox(source);
    }

    @Nullable
    private Date getSelectedDate()
    {
        if (injectionDatePicker == null) return null;

        return DateUtil.getDayStart(Date.from(
                injectionDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private void populateDataBox(DataSource source)
    {
        injectionDataBox.getChildren().clear();

        if (fieldCache.containsKey(source))
        {
            // We already have it in the cache, no need to inflate again
            for (RawDataField wrapper : fieldCache.get(source))
            {
                injectionDataBox.getChildren().add(wrapper.inflatedParent);
            }
        }
        else
        {
            // We do not have it in the cache, it must be inflated
            Class<? extends Data> dataClazz = source.getDataClass();
            Field[] fields = dataClazz.getDeclaredFields();

            fieldCache.put(source, new HashSet<>());
            for (Field field : fields)
            {
                if (!field.isAnnotationPresent(RawDataElementAnnotation.class))
                    continue;

                RawDataField wrapper = RawDataField.newInstance(model, source, field);
                wrapper.inflateAsync(injectionDataBox);
                fieldCache.get(source).add(wrapper);
            }
        }
    }

    private void onInjectClicked()
    {
        TimeSeries series = model.getCurrentlySelectedTimeSeries();
        if(series == null)
        {
            controller.snackbarNullTimeSeries();
            return;
        }

        final Date date = getSelectedDate();
        if (date == null)
        {
            controller.snackbar(Controller.SnackbarType.ERROR, "Select a date to inject the data on!");
            return;
        }

        HashSet<RawDataField> fields = fieldCache.get(model.getCurrentlySelectedInjectionDataSource());
        for (RawDataField field : fields)
            if (!field.isInflated()) return;

        Map<RawDataField, Object> fieldValues = new LinkedHashMap<>();
        for (RawDataField field : fields)
        {
            try
            {
                fieldValues.put(field, field.getValue());
            }
            catch (NumberFormatException ignored)
            {
                controller.snackbar(Controller.SnackbarType.ERROR, "Unable to parse number " + field.fieldNameFormatted);
                return;
            }
        }

        // Run in background thread since we're reflecting quite heavily
        final DataSource source = model.getCurrentlySelectedInjectionDataSource();
        runBgThread(framework ->
        {
            // Reflect the Constructor
            Tuple<Constructor<? extends Data>, Object[]> constructorTuple =
                    createConstructorTuple(source, date, fieldValues);

            try
            {
                Constructor<? extends Data> constructor = constructorTuple.getA();
                Object[] params = constructorTuple.getB();
                injectData(framework, series, source, constructor.newInstance(params));

                runUIThread(() ->
                {
                    controller.snackbar(Controller.SnackbarType.INFO,
                                        "Injected 1 " + source.getDataType().getName() + " into " + source.getName());

                    // Update the data count in the data present overview tab to reflect these changes
                    ((OverviewPaneHandler) controller.getPaneHandler(DashboardPane.OVERVIEW)).updateDataPresent(series);
                });
            }
            catch (Exception ignored) { }
        });
    }

    private Tuple<Constructor<? extends Data>, Object[]> createConstructorTuple(DataSource source, Date date,
                                                                                Map<RawDataField, Object> fieldValues)
    {
        // Find the correct Constructor
        Class<? extends Data> dataClazz = source.getDataClass();
        Constructor<? extends Data> constructor =
                (Constructor<? extends Data>) ReflectionUtil.getConstructorByAnnotation(dataClazz,
                                                                                        RawDataAnnotation.class);
        RawDataAnnotation annotation = constructor.getAnnotation(RawDataAnnotation.class);
        assert (constructor.getParameterCount() == 1 + annotation.paramOrder().length);

        // Create the parameters array (in the correct order; date included)
        Object[] params = new Object[constructor.getParameterCount()];
        params[annotation.indexOfDate()] = date;
        int currentIndex = 0;
        for (String param : annotation.paramOrder())
        {
            if (currentIndex == annotation.indexOfDate()) currentIndex ++;

            // This index is where RawDataElementAnnotation annotated fields go
            inner: for (Map.Entry<RawDataField, Object> entry : fieldValues.entrySet())
            {
                if (entry.getKey().fieldName.equals(param))
                {
                    params[currentIndex] = entry.getValue();
                    break inner;
                }
            }

            currentIndex ++;
        }

        return new Tuple<>(constructor, params);
    }

    /**
     *
     * @param framework
     * @param series
     * @param source
     * @param data
     * @return 1 if data was successfully injected, otherwise unsuccessfully
     */
    private void injectData(Framework framework, TimeSeries series, DataSource source, Data data)
    {
        TimeSeriesManager manager = framework.getTimeSeriesManager();

        // Write the data to the file
        if (data.getType().equals(DataType.DOCUMENT))
            manager.onDataCollected(series, source, Arrays.asList((Document) data), false);
        else if (data.getType().equals(DataType.STOCK_QUOTE))
            manager.onDataCollected(series, source, Arrays.asList((StockQuote) data), false);
    }

    public static class RawDataField
    {
        private enum Type { DOUBLE, STRING, ENUM }

        private final DashboardModel model;
        private final DataSource source;
        private final Type type;
        private final String fieldName;
        private final String fieldNameFormatted;
        private final Class<?> fieldType;

        private boolean inflated;
        private Parent inflatedParent;
        private Node content;

        private RawDataField(DashboardModel model, DataSource source, Type type, String fieldName, Class<?> fieldType)
        {
            this.model = model;
            this.source = source;
            this.type = type;
            this.fieldName = fieldName;
            this.fieldNameFormatted = StringUtil.camelCaseToWords(fieldName, true)
                                                    .stream()
                                                    .collect(Collectors.joining(" "));
            this.fieldType = fieldType;
        }

        @Override
        public int hashCode()
        {
            return fieldName.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            return (obj instanceof RawDataField) ? obj.hashCode() == hashCode() : false;
        }

        public void inflateAsync(final VBox addTo)
        {
            SceneHelper.inflateAsync(GENERIC_INPUT_FIELD_FXML, result ->
            {
                if (!result.isSuccess()) return;

                FXMLLoader loader = result.getLoader();
                Parent parent = result.getLoaded();

                Label label = (Label) loader.getNamespace().get("header");
                Pane contentPane = (Pane) loader.getNamespace().get("contentPane");
                modifyNodes(label, contentPane);

                inflated = true;
                inflatedParent = parent;

                // The user may have changed the data source in the time between this function call and the inflation
                // task being performed; so we'll only add if they're still viewing it
                if (model.getCurrentlySelectedInjectionDataSource().equals(source))
                    addTo.getChildren().add(parent);
            });
        }

        private void modifyNodes(Label label, Pane contentPane)
        {
            label.setText(fieldNameFormatted);

            switch (type)
            {
                case DOUBLE:
                case STRING:
                    content = createTextField(contentPane);
                    break;
                case ENUM:
                    content = createEnumComboBox(contentPane);
                    break;
            }

            contentPane.getChildren().add(content);
        }

        private JFXTextField createTextField(Pane contentPane)
        {
            JFXTextField textField = new JFXTextField();
            textField.prefWidthProperty().bind(
                    contentPane.prefWidthProperty().multiply(GENERIC_INPUT_FIELD_WIDTH_BIND_COEFF));
            return textField;
        }

        private ComboBox<String> createEnumComboBox(Pane contentPane)
        {
            Class<? extends Enum> clazz = (Class<? extends Enum>) fieldType;
            String[] choices = Arrays.stream(clazz.getEnumConstants())
                                     .map(c -> c.name())
                                     .toArray(String[]::new);

            JFXComboBox<String> comboBox = new JFXComboBox<>();
            comboBox.getItems().addAll(choices);
            comboBox.getSelectionModel().select(0);
            comboBox.prefWidthProperty().bind(
                    contentPane.prefWidthProperty().multiply(GENERIC_INPUT_FIELD_WIDTH_BIND_COEFF));
            return comboBox;
        }

        public boolean isInflated()
        {
            return inflated;
        }

        /**
         * Must be inflated before the value can be retrieved.
         * @return
         */
        public Object getValue() throws NumberFormatException
        {
            switch (type)
            {
                case DOUBLE:
                    return Double.valueOf(((JFXTextField) content).getText());
                case STRING:
                    return ((JFXTextField) content).getText();
                case ENUM:
                    Class<? extends Enum> enumClazz = (Class<? extends Enum>) fieldType;
                    Enum<?>[] enums = enumClazz.getEnumConstants();
                    return enums[ ((JFXComboBox) content).getSelectionModel().getSelectedIndex() ];
            }
            return null;
        }

        public static RawDataField newInstance(DashboardModel model, DataSource source, Field field)
        {
            return new RawDataField(model, source, typeFromClazz(field.getType()), field.getName(), field.getType());
        }

        private static Type typeFromClazz(Class<?> clazz)
        {
            if (clazz == double.class)
            {
                return Type.DOUBLE;
            }
            else if (clazz == String.class)
            {
                return Type.STRING;
            }
            else if (clazz.isEnum())
            {
                return Type.ENUM;
            }
            return null;
        }

    }

}
