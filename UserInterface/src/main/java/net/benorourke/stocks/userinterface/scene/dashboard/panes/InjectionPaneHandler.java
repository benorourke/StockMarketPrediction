package net.benorourke.stocks.userinterface.scene.dashboard.panes;

import com.jfoenix.controls.JFXComboBox;
import javafx.scene.layout.VBox;
import net.benorourke.stocks.framework.collection.datasource.DataSource;
import net.benorourke.stocks.framework.series.TimeSeries;
import net.benorourke.stocks.framework.series.data.Data;
import net.benorourke.stocks.framework.series.data.RawDataElementAnnotation;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardController;
import net.benorourke.stocks.userinterface.scene.dashboard.DashboardModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InjectionPaneHandler extends PaneHandler
{
    private final JFXComboBox injectionSourceComboBox;
    private final VBox injectionDataBox;



    public InjectionPaneHandler(DashboardController controller, DashboardModel model,
                                JFXComboBox injectionSourceComboBox, VBox injectionDataBox)
    {
        super(controller, model);

        this.injectionSourceComboBox = injectionSourceComboBox;
        this.injectionDataBox = injectionDataBox;
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
                    selectCollectionDataSource(sources.get(strSources.indexOf(newValue))));

            // Select the first source - there's several inbuilt ones so this is safe
            injectionSourceComboBox.getSelectionModel().select(0);
        });
    }

    @Override
    public void onTimeSeriesChanged(TimeSeries series) { }

    private void selectCollectionDataSource(DataSource source)
    {
        model.setCurrentlySelectedInjectionDataSource(source);
        populateDataBox(source);
    }

    private void populateDataBox(DataSource source)
    {
        Class<? extends Data> dataClazz = source.getDataClass();
        Field[] fields = dataClazz.getDeclaredFields();

        List<Field> rawDataFields = new ArrayList<>();
        for (Field field : fields)
        {
            if (!field.isAnnotationPresent(RawDataElementAnnotation.class))
                continue;

            rawDataFields.add(field);
        }
    }

    public class DataSourceField
    {

    }

}
