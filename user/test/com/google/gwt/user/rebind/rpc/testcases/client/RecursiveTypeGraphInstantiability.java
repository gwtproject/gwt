package com.google.gwt.user.rebind.rpc.testcases.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.CustomFieldSerializer;
import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public interface RecursiveTypeGraphInstantiability extends IsSerializable {
  public static interface StoredDataMiningReportDTO extends NamedWithStringID, Renamable {
    DataMiningReportDTO getReport();
  }

  public static interface NamedWithStringID extends NamedWithID {
    @Override
    String getId();
  }

  public static interface NamedWithID extends Named, WithID {
  }

  public static interface Named extends Serializable {
    String getName();
  }

  public static interface WithID {
    /**
     * Something that uniquely identifies this object beyond its name
     */
    Serializable getId();
  }

  public static interface Renamable extends Named {
    void setName(String newName);
  }

  public static interface DataMiningReportDTO extends Serializable {
  }

  public static class RenamableImpl implements Renamable {
    private static final long serialVersionUID = -4815125282671451300L;
    private String name;

    public RenamableImpl(String name) {
      super();
      this.name = name;
    }

    @Override
    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return getName();
    }

    @Override
    public void setName(String newName) {
      this.name = newName;
    }
  }

  public static class StoredDataMiningReportDTOImpl extends RenamableImpl implements
      StoredDataMiningReportDTO {
    private static final long serialVersionUID = 9218620680326470175L;

    private String id;
    private DataMiningReportDTO report;

    @Deprecated // for GWT serialization only
    StoredDataMiningReportDTOImpl() {
      super(null);
    }

    public StoredDataMiningReportDTOImpl(String id, String name, DataMiningReportDTO report) {
      super(name);
      this.id = id;
      this.report = report;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public DataMiningReportDTO getReport() {
      return report;
    }
  }

  public static interface FilterDimensionParameter extends NamedWithStringID {
    String getTypeName();
  }

  public abstract class AbstractParameterizedDimensionFilter extends RenamableImpl implements
      FilterDimensionParameter {
    private static final long serialVersionUID = 3853015601496471357L;

    private String typeName;
    private String id;

    @Deprecated // GWT serialization only
    AbstractParameterizedDimensionFilter() {
      super(null);
    }

    public AbstractParameterizedDimensionFilter(String name, String typeName) {
      super(name);
      this.id = "abc";
      this.typeName = typeName;
    }

    @Override
    public String getId() {
      return id;
    }

    @Override
    public String getTypeName() {
      return typeName;
    }
  }

  public class ValueListFilterParameter extends AbstractParameterizedDimensionFilter {
    private static final long serialVersionUID = -8440835683986197499L;

    private HashSet<? extends Serializable> values;

    private transient Set<ParameterModelListener> parameterModelListeners;

    private HashSet<ParameterModelListener> nonTransientParameterModelListeners;

    @Deprecated // GWT serialization only
    ValueListFilterParameter() {
    }

    public <T extends Serializable> ValueListFilterParameter(String name, String typeName,
        Iterable<T> values) {
      super(name, typeName);
      final HashSet<T> set = new HashSet<>();
      this.values = set;
      this.parameterModelListeners = new HashSet<>();
      this.nonTransientParameterModelListeners = new HashSet<>();
    }

    public Iterable<? extends Serializable> getValues() {
      return new HashSet<>(values);
    }

    public Set<ParameterModelListener> getParameterModelListeners() {
      return parameterModelListeners;
    }

    public void setParameterModelListeners(Set<ParameterModelListener> parameterModelListeners) {
      this.parameterModelListeners = parameterModelListeners;
    }

    public HashSet<ParameterModelListener> getNonTransientParameterModelListeners() {
      return nonTransientParameterModelListeners;
    }

    public void setNonTransientParameterModelListeners(
        HashSet<ParameterModelListener> nonTransientParameterModelListeners) {
      this.nonTransientParameterModelListeners = nonTransientParameterModelListeners;
    }
  }

  public static interface StatisticQueryDefinitionDTO extends Serializable {
  }

  public interface ParameterModelListener {
  }

  public class ParameterValueChangeListener implements ParameterModelListener, Serializable {
    private static final long serialVersionUID = 402977347893177440L;

    private ModifiableDataMiningReportDTO report;
    
    @Deprecated // for GWT serialization only
    ParameterValueChangeListener() {
    }
    
    public ParameterValueChangeListener(ModifiableDataMiningReportDTO report) {
      super();
      this.report = report;
    }

    public ModifiableDataMiningReportDTO getReport() {
      return report;
    }

    public void setReport(ModifiableDataMiningReportDTO report) {
      this.report = report;
    }
  }

  public class LocalizedTypeDTO implements Serializable {
    private static final long serialVersionUID = -5976605483497225403L;

    private String typeName;
    private String displayName;

    /**
     * <b>Constructor for the GWT-Serialization. Don't use this!</b>
     */
    @Deprecated
    LocalizedTypeDTO() {
    }

    public LocalizedTypeDTO(String typeName, String displayName) {
      this.typeName = typeName;
      this.displayName = displayName;
    }

    public String getTypeName() {
      return typeName;
    }

    public String getDisplayName() {
      return displayName;
    }
  }

  public static abstract class SerializableSettings extends AbstractSettings implements
      Serializable {
    private static final long serialVersionUID = -2710627501473421333L;
  }

  public abstract class PolarDataMiningSettings extends SerializableSettings {

    private static final long serialVersionUID = 3670315004615987482L;

    public abstract Integer getMinimumDataCountPerGraph();

    public abstract double getMinimumWindConfidence();

    public abstract boolean applyMinimumWindConfidence();

    public abstract Integer getMinimumDataCountPerAngle();

    public abstract int getNumberOfHistogramColumns();

    public abstract boolean useOnlyWindGaugesForWindSpeed();

    public abstract boolean useOnlyEstimatedForWindDirection();

    public abstract WindSpeedSteppingWithMaxDistance getWindSpeedStepping();

    public abstract boolean areDefault();

  }

  public interface WindSpeedStepping extends Serializable {

    public abstract int getLevelIndexForValue(double speed);

    public abstract Double getSteppedValueForValue(double speed);

    double[] getRawStepping();

    public abstract int getLevelIndexFloorForValue(double speed);

    public abstract int getLevelIndexCeilingForValue(double speed);

    double getDistanceToLevelFloor(double speed);

    int hashCode();

    boolean equals(Object obj);

  }

  public class WindSpeedSteppingImpl implements WindSpeedStepping {

    // For GWT Serialization
    protected WindSpeedSteppingImpl() {
    };

    private static final long serialVersionUID = 2215693490331489508L;
    protected double[] levels;

    public WindSpeedSteppingImpl(double[] levels) {
      this.levels = levels;
    }

    public int getNumberOfLevels() {
      return levels.length;
    }

    @Override
    public int getLevelIndexForValue(double speed) {
      return 0;
    }

    @Override
    public Double getSteppedValueForValue(double speed) {
      return null;
    }

    @Override
    public double[] getRawStepping() {
      return null;
    }

    @Override
    public int getLevelIndexFloorForValue(double speed) {
      return 0;
    }

    @Override
    public int getLevelIndexCeilingForValue(double speed) {
      return 0;
    }

    @Override
    public double getDistanceToLevelFloor(double speed) {
      return 0;
    }
  }

  public class WindSpeedSteppingWithMaxDistance extends WindSpeedSteppingImpl {

    private static final long serialVersionUID = -2207840179212727591L;
    private double maxDistance;

    // For GWT Serialization
    WindSpeedSteppingWithMaxDistance() {
      super();
    };

    public WindSpeedSteppingWithMaxDistance(double[] levels, double maxDistance) {
      super(levels);
      this.maxDistance = maxDistance;
    }

    @Override
    public int getLevelIndexForValue(double speed) {
      return -1;
    }

    public double getMaxDistance() {
      return maxDistance;
    }
  }
  public class PolarDataMiningSettingsImpl extends PolarDataMiningSettings {
    private static final long serialVersionUID = 2731616509404813790L;
    private Integer minimumDataCountPerGraph;
    private double minimumWindConfidence;
    private Integer minimumDataCountPerAngle;
    private Integer numberOfHistogramColumns;
    private boolean useOnlyWindGaugesForWindSpeed;
    private boolean useOnlyEstimationForWindDirection;
    private WindSpeedSteppingWithMaxDistance windStepping;
    private boolean applyMinimumWindConfidence;

    // GWT
    PolarDataMiningSettingsImpl() {
    };

    public PolarDataMiningSettingsImpl(Integer minimumDataCountPerGraph,
        double minimumWindConfidence, boolean applyMinimumWindConfidence,
        Integer minimumDataCountPerAngle, Integer numberOfHistogramColumns,
        boolean useOnlyWindGaugesForWindSpeed, boolean useOnlyEstimationForWindDirection,
        WindSpeedSteppingWithMaxDistance windStepping) {
      this.minimumDataCountPerGraph = minimumDataCountPerGraph;
      this.minimumWindConfidence = minimumWindConfidence;
      this.applyMinimumWindConfidence = applyMinimumWindConfidence;
      this.minimumDataCountPerAngle = minimumDataCountPerAngle;
      this.numberOfHistogramColumns = numberOfHistogramColumns;
      this.useOnlyWindGaugesForWindSpeed = useOnlyWindGaugesForWindSpeed;
      this.useOnlyEstimationForWindDirection = useOnlyEstimationForWindDirection;
      this.windStepping = windStepping;
    }

    @Override
    public Integer getMinimumDataCountPerGraph() {
      return minimumDataCountPerGraph;
    }

    @Override
    public double getMinimumWindConfidence() {
      return minimumWindConfidence;
    }

    @Override
    public Integer getMinimumDataCountPerAngle() {
      return minimumDataCountPerAngle;
    }

    @Override
    public int getNumberOfHistogramColumns() {
      return numberOfHistogramColumns;
    }

    @Override
    public boolean useOnlyWindGaugesForWindSpeed() {
      return useOnlyWindGaugesForWindSpeed;
    }

    @Override
    public boolean useOnlyEstimatedForWindDirection() {
      return useOnlyEstimationForWindDirection;
    }

    @Override
    public WindSpeedSteppingWithMaxDistance getWindSpeedStepping() {
      return windStepping;
    }

    @Override
    public boolean applyMinimumWindConfidence() {
      return applyMinimumWindConfidence;
    }

    @Override
    public boolean areDefault() {
      return false;
    }
  }

  public static class DataRetrieverLevelDTO implements Serializable,
      Comparable<DataRetrieverLevelDTO> {
    private static final long serialVersionUID = 6911713148350359643L;

    /**
     * The index of this retriever level in the retriever chain.
     */
    private int retrieverLevel;
    /**
     * The fully qualified name of the Processor performing the retrieval of this level.
     */
    private String retrieverTypeName;
    /**
     * The type of the retrieved data elements in form of a {@link LocalizedTypeDTO}. Its type name
     * is the fully qualified name of the retrieved data type and is used for identification
     * purposes.
     * 
     * The display name is used as human readable string representation of this retriever level and
     * should be omitted when persisting a DataRetrieverLevelDTO.
     */
    private LocalizedTypeDTO retrievedDataType;

    /**
     * The default settings for this retriever level or <code>null</code>, if the level doesn't have
     * settings. Should be omitted when persisting a DataRetrieverLevelDTO.
     */
    private SerializableSettings defaultSettings;

    /**
     * <b>Constructor for the GWT-Serialization. Don't use this!</b>
     */
    @Deprecated
    DataRetrieverLevelDTO() {
    }

    public DataRetrieverLevelDTO(int retrieverLevel, String retrieverTypeName,
        LocalizedTypeDTO retrievedDataType, SerializableSettings defaultSettings) {
      this.retrieverLevel = retrieverLevel;
      this.retrieverTypeName = retrieverTypeName;
      this.retrievedDataType = retrievedDataType;
      this.defaultSettings = defaultSettings;
    }

    public int getLevel() {
      return retrieverLevel;
    }

    public String getRetrieverTypeName() {
      return retrieverTypeName;
    }

    public LocalizedTypeDTO getRetrievedDataType() {
      return retrievedDataType;
    }

    public boolean hasSettings() {
      return getDefaultSettings() != null;
    }

    public SerializableSettings getDefaultSettings() {
      return defaultSettings;
    }

    @Override
    public int compareTo(DataRetrieverLevelDTO o) {
      return 0;
    }
  }

  public static class FilterDimensionIdentifier implements Serializable {
    private static final long serialVersionUID = -4824907338023614296L;
    private DataRetrieverLevelDTO retrieverLevel;
    private FunctionDTO dimensionFunction;

    @Deprecated // for GWT serialization only
    FilterDimensionIdentifier() {
    }

    public FilterDimensionIdentifier(DataRetrieverLevelDTO retrieverLevel,
        FunctionDTO dimensionFunction) {
      super();
      this.retrieverLevel = retrieverLevel;
      this.dimensionFunction = dimensionFunction;
    }

    public DataRetrieverLevelDTO getRetrieverLevel() {
      return retrieverLevel;
    }

    public FunctionDTO getDimensionFunction() {
      return dimensionFunction;
    }
  }

  public static class ModifiableDataMiningReportDTO implements DataMiningReportDTO {
    private static final long serialVersionUID = -6512175470789118223L;

    /**
     * Handled by identity; remove and contains checks don't use query equality because modifiable
     * queries can change their equality/hashCode over their life cycle
     */
    private ArrayList<ModifiableStatisticQueryDefinitionDTO> queryDefinitions;
    private HashSet<FilterDimensionParameter> parameters;
    private IdentityHashMap<StatisticQueryDefinitionDTO, HashMap<FilterDimensionIdentifier, FilterDimensionParameter>> parameterUsages;

    @SuppressWarnings("unused") // only to force the type to be instantiable for GWT serialization
    private ParameterModelListener _serializationDummy = null;

    /**
     * Objects of this type entertain one value change listener for each parameter in
     * {@link #getParameters()} so that when a parameter value changes, all
     * {@link #getQueryDefinitions() queries in this report} using this parameter will have their
     * {@link StatisticQueryDefinitionDTO#getFilterSelection() filter selections} adjusted
     * accordingly.
     */
    private HashMap<FilterDimensionParameter, ParameterModelListener> parameterValueChangeListeners;

    private transient Set<ParameterModelListener> parameterModelListeners;

    /**
     * Creates an empty report with no queries and hence no parameter usages.
     */
    public ModifiableDataMiningReportDTO() {
      this(Collections.emptySet(), Collections.emptySet());
    }

    public ModifiableDataMiningReportDTO(
        Iterable<ModifiableStatisticQueryDefinitionDTO> queryDefinitions,
        Iterable<FilterDimensionParameter> parameters) {
    }

    public HashMap<FilterDimensionParameter, ParameterModelListener> getParameterValueChangeListeners() {
      return parameterValueChangeListeners;
    }

    public void setParameterValueChangeListeners(
        HashMap<FilterDimensionParameter, ParameterModelListener> parameterValueChangeListeners) {
      this.parameterValueChangeListeners = parameterValueChangeListeners;
    }

    public Set<ParameterModelListener> getParameterModelListeners() {
      return parameterModelListeners;
    }

    public void setParameterModelListeners(Set<ParameterModelListener> parameterModelListeners) {
      this.parameterModelListeners = parameterModelListeners;
    }

    public ArrayList<ModifiableStatisticQueryDefinitionDTO> getQueryDefinitions() {
      return queryDefinitions;
    }

    public void setQueryDefinitions(
        ArrayList<ModifiableStatisticQueryDefinitionDTO> queryDefinitions) {
      this.queryDefinitions = queryDefinitions;
    }

    public HashSet<FilterDimensionParameter> getParameters() {
      return parameters;
    }

    public void setParameters(HashSet<FilterDimensionParameter> parameters) {
      this.parameters = parameters;
    }

    public IdentityHashMap<StatisticQueryDefinitionDTO, HashMap<FilterDimensionIdentifier, FilterDimensionParameter>> getParameterUsages() {
      return parameterUsages;
    }

    public void setParameterUsages(
        IdentityHashMap<StatisticQueryDefinitionDTO, HashMap<FilterDimensionIdentifier, FilterDimensionParameter>> parameterUsages) {
      this.parameterUsages = parameterUsages;
    }
  }

  public class DataRetrieverChainDefinitionDTO implements Serializable,
      Comparable<DataRetrieverChainDefinitionDTO> {
    private static final long serialVersionUID = 7806173601799997214L;

    /**
     * The fully qualified name of the initial data source type.
     */
    private String dataSourceTypeName;
    /**
     * The ordered list of retriever levels.
     */
    private ArrayList<DataRetrieverLevelDTO> retrieverLevels;

    /**
     * A human readable string representation. Should be omitted when persisting a
     * DataRetrieverChainDefinition.
     */
    private String displayName;

    /**
     * <b>Constructor for the GWT-Serialization. Don't use this!</b>
     */
    @Deprecated
    DataRetrieverChainDefinitionDTO() {
    }

    public DataRetrieverChainDefinitionDTO(String name, String dataSourceTypeName,
        ArrayList<DataRetrieverLevelDTO> retrieverLevels) {
      this.displayName = name;
      this.dataSourceTypeName = dataSourceTypeName;
      this.retrieverLevels = new ArrayList<>(retrieverLevels);
    }

    public String getName() {
      return displayName;
    }

    public String getDataSourceTypeName() {
      return dataSourceTypeName;
    }

    public String getRetrievedDataTypeName() {
      return retrieverLevels.get(retrieverLevels.size() - 1).getRetrievedDataType().getTypeName();
    }

    public ArrayList<DataRetrieverLevelDTO> getRetrieverLevels() {
      return retrieverLevels;
    }

    public int getLevelAmount() {
      return retrieverLevels.size();
    }

    @Override
    public int compareTo(DataRetrieverChainDefinitionDTO o) {
      return 0;
    }
  }

  public static class ModifiableStatisticQueryDefinitionDTO implements StatisticQueryDefinitionDTO {
    private static final long serialVersionUID = -6438771277564908352L;

    /**
     * The extraction function used to get the statistic from the retrieved data elements.
     */
    private FunctionDTO statisticToCalculate;
    /**
     * The aggregator used to aggregate the extracted statistics.
     */
    private AggregationProcessorDefinitionDTO aggregatorDefinition;
    /**
     * The list of dimensions to group the results by.
     */
    private ArrayList<FunctionDTO> dimensionsToGroupBy;
    /**
     * The retriever chain used to retrieve the data elements.
     */
    private DataRetrieverChainDefinitionDTO dataRetrieverChainDefinition;
    /**
     * The settings to be used for the retriever levels. Can be empty, if no retriever level has
     * settings.
     */
    private HashMap<DataRetrieverLevelDTO, SerializableSettings> retrieverSettings;
    /**
     * The values used to filter the data elements during the retrieval process. Each set of values
     * is mapped by the dimension used to extract the value from a data element and the retriever
     * level the dimension belongs to. The dimensions are declared by the data type retrieved by the
     * corresponding retriever level and not by the data type the query is based on (return type of
     * the retriever chain).
     * 
     * The actual filter values can be anything that is returned by a dimension, which will be
     * mostly simple data like Strings or Enums, but can also be more complex data structures like
     * {@link ClusterDTO}.
     */
    private HashMap<DataRetrieverLevelDTO, HashMap<FunctionDTO, HashSet<? extends Serializable>>> filterSelection;

    /**
     * The LocalInfo name that will be used for internationalization. Should be omitted when
     * persisting a ModifiableStatisticQueryDefinitionDTO.
     */
    private String localeInfoName;

    /**
     * Used to record whether this query has changed since last run. When, for example, a parameter
     * change triggers the modification of this query's dimension filter(s), the flag should be
     * {@link #setQueryChangedSinceLastRun(boolean) set}. It must be un-set when the query is run.
     */
    private transient boolean queryChangedSinceLastRun;

    /**
     * <b>Constructor for the GWT-Serialization. Don't use this!</b>
     */
    @Deprecated
    ModifiableStatisticQueryDefinitionDTO() {
    }

    public ModifiableStatisticQueryDefinitionDTO(String localeInfoName,
        FunctionDTO statisticToCalculate, AggregationProcessorDefinitionDTO aggregatorDefinition,
        DataRetrieverChainDefinitionDTO dataRetrieverChainDefinition) {
      this.localeInfoName = localeInfoName;
      this.statisticToCalculate = statisticToCalculate;
      this.aggregatorDefinition = aggregatorDefinition;
      this.dataRetrieverChainDefinition = dataRetrieverChainDefinition;
      this.retrieverSettings = new HashMap<>();
      this.filterSelection = new HashMap<>();
      this.dimensionsToGroupBy = new ArrayList<FunctionDTO>();
    }

    public ModifiableStatisticQueryDefinitionDTO(StatisticQueryDefinitionDTO definition) {
    }

    public FunctionDTO getStatisticToCalculate() {
      return statisticToCalculate;
    }

    public AggregationProcessorDefinitionDTO getAggregatorDefinition() {
      return aggregatorDefinition;
    }

    public DataRetrieverChainDefinitionDTO getDataRetrieverChainDefinition() {
      return dataRetrieverChainDefinition;
    }

    public String getLocaleInfoName() {
      return localeInfoName;
    }

    public boolean isQueryChangedSinceLastRun() {
      return queryChangedSinceLastRun;
    }

    public void setQueryChangedSinceLastRun(boolean queryChangedSinceLastRun) {
      this.queryChangedSinceLastRun = queryChangedSinceLastRun;
    }

    public void setDataRetrieverChainDefinition(
        DataRetrieverChainDefinitionDTO dataRetrieverChainDefinition) {
      if (dataRetrieverChainDefinition == null) {
        throw new NullPointerException("The data retriever chain definition mustn't be null");
      }
      this.dataRetrieverChainDefinition = dataRetrieverChainDefinition;
    }

    public void setRetrieverSettings(DataRetrieverLevelDTO retrieverLevel,
        SerializableSettings settings) {
      if (retrieverLevel == null) {
        throw new NullPointerException("The retriever level mustn't be null");
      }
      retrieverSettings.put(retrieverLevel, settings);
    }

    public void setFilterSelectionFor(DataRetrieverLevelDTO retrieverLevel,
        HashMap<FunctionDTO, HashSet<? extends Serializable>> levelFilterSelection) {
      if (retrieverLevel == null) {
        throw new NullPointerException("The retriever level mustn't be null");
      }
      if (levelFilterSelection == null) {
        throw new NullPointerException("The level filter selection mustn't be null");
      }
      filterSelection.put(retrieverLevel, levelFilterSelection);
    }

    public void appendDimensionToGroupBy(FunctionDTO dimensionToGroupBy) {
      if (dimensionToGroupBy == null) {
        throw new NullPointerException("The dimension mustn't be null");
      }
      dimensionsToGroupBy.add(dimensionToGroupBy);
    }

    public void setStatisticToCalculate(FunctionDTO statisticToCalculate) {
      if (statisticToCalculate == null) {
        throw new NullPointerException("The statistic to calculate mustn't be null");
      }
      this.statisticToCalculate = statisticToCalculate;
    }

    public void setAggregatorDefinition(AggregationProcessorDefinitionDTO aggregatorDefinition) {
      if (aggregatorDefinition == null) {
        throw new NullPointerException("The aggregator definition mustn't be null");
      }
      this.aggregatorDefinition = aggregatorDefinition;
    }

    public void setLocaleInfoName(String localeInfoName) {
      if (localeInfoName == null) {
        throw new NullPointerException("The locale info name mustn't be null");
      }
      this.localeInfoName = localeInfoName;
    }
  }

  public static class AggregationProcessorDefinitionDTO implements Serializable,
      Comparable<AggregationProcessorDefinitionDTO> {
    private static final long serialVersionUID = -434497637456305118L;

    /**
     * Additional identification feature, since the extracted and aggregated type wouldn't suffice.
     * Has to be included when persisting a AggregationProcessorDefinitionDTO.
     */
    private String messageKey;
    /**
     * The fully qualified name of the aggregators input type.
     */
    private String extractedTypeName;
    /**
     * The fully qualified name of the aggregators result type.
     */
    private String aggregatedTypeName;

    /**
     * A human readable string representation. Should be omitted when persisting a
     * AggregationProcessorDefinitionDTO.
     */
    private String displayName;

    /**
     * <b>Constructor for the GWT-Serialization. Don't use this!</b>
     */
    @Deprecated
    AggregationProcessorDefinitionDTO() {
    }

    public AggregationProcessorDefinitionDTO(String messageKey, String extractedTypeName,
        String aggregatedTypeName, String displayName) {
      this.messageKey = messageKey;
      this.extractedTypeName = extractedTypeName;
      this.aggregatedTypeName = aggregatedTypeName;
      this.displayName = displayName;
    }

    public String getMessageKey() {
      return messageKey;
    }

    public String getExtractedTypeName() {
      return extractedTypeName;
    }

    public String getAggregatedTypeName() {
      return aggregatedTypeName;
    }

    public String getDisplayName() {
      return displayName;
    }

    @Override
    public String toString() {
      return getExtractedTypeName() + " -> " + getAggregatedTypeName() + "[messageKey: "
          + messageKey + "]";
    }

    @Override
    public int compareTo(AggregationProcessorDefinitionDTO aggregatorDefinitionDTO) {
      return getDisplayName().compareTo(aggregatorDefinitionDTO.getDisplayName());
    }
  }

  public static interface Settings {
  }

  public static abstract class AbstractSettings implements Settings {
  }

  public static class FunctionDTO implements Serializable, Comparable<FunctionDTO> {
    private static final long serialVersionUID = 4587389541910498505L;

    private final boolean isDimension;
    /**
     * The function's name including its parameters in parenthesis or empty parenthesis, if this
     * function doesn't have parameters. Can consist of multiple concatenated function names, if the
     * backend Function encapsulated multiple data mining functions.
     */
    private final String functionName;
    /**
     * The fully qualified name of the type that declares this function.
     */
    private final String sourceTypeName;
    /**
     * The fully qualified name of the type returned by this function.
     */
    private final String returnTypeName;
    /**
     * The fully qualified names of the functions parameter types.
     */
    private final List<String> parameterTypeNames;

    /**
     * Meta-data for the natural ordering of FunctionDTOs. Should be omitted when persisting a
     * FunctionDTO.
     */
    private final int ordinal;
    /**
     * A human readable string representation. Should be omitted when persisting a FunctionDTO.
     */
    private String displayName;
    /**
     * If the {@link #displayName} is not set and this provider is valid, the {@F
     */
    private transient DisplayNameProvider displayNameProvider;

    public static interface DisplayNameProvider {
      String getDisplayName();
    }

    /**
     * Makes the construction of the {@link #displayName} lazy, using the
     * {@code displayNameProvider}.
     */
    public FunctionDTO(boolean isDimension, String functionName, String sourceTypeName,
        String returnTypeName, List<String> parameterTypeNames,
        DisplayNameProvider displayNameProvider, int ordinal) {
      this(isDimension, functionName, sourceTypeName, returnTypeName, parameterTypeNames,
          /* displayName is constructed lazily using the displayNameProvider */ (String) null,
          ordinal);
      this.displayNameProvider = displayNameProvider;
    }

    public FunctionDTO(boolean isDimension, String functionName, String sourceTypeName,
        String returnTypeName, List<String> parameterTypeNames, String displayName, int ordinal) {
      this.isDimension = isDimension;
      this.functionName = functionName;
      this.sourceTypeName = sourceTypeName;
      this.returnTypeName = returnTypeName;
      this.parameterTypeNames = new ArrayList<String>(parameterTypeNames);
      this.displayName = displayName;
      this.ordinal = ordinal;
    }

    public String getSourceTypeName() {
      return sourceTypeName;
    }

    public String getReturnTypeName() {
      return returnTypeName;
    }

    public List<String> getParameterTypeNames() {
      return parameterTypeNames;
    }

    public String getFunctionName() {
      return functionName;
    }

    public String getDisplayName() {
      if (displayName == null && displayNameProvider != null) {
        displayName = displayNameProvider.getDisplayName();
      }
      return displayName;
    }

    public boolean isDimension() {
      return isDimension;
    }

    public int getOrdinal() {
      return ordinal;
    }

    @Override
    public int compareTo(FunctionDTO f) {
      return Integer.compare(this.getOrdinal(), f.getOrdinal());
    }
  }

  public class FunctionDTO_CustomFieldSerializer extends CustomFieldSerializer<FunctionDTO> {
    @Override
    public void serializeInstance(SerializationStreamWriter streamWriter, FunctionDTO instance)
        throws SerializationException {
      serialize(streamWriter, instance);
    }

    public static void serialize(SerializationStreamWriter streamWriter, FunctionDTO instance)
        throws SerializationException {
      streamWriter.writeBoolean(instance.isDimension());
      streamWriter.writeString(instance.getFunctionName());
      streamWriter.writeString(instance.getSourceTypeName());
      streamWriter.writeString(instance.getReturnTypeName());
      streamWriter.writeObject(instance.getParameterTypeNames());
      streamWriter.writeString(instance.getDisplayName());
      streamWriter.writeInt(instance.getOrdinal());
    }

    @Override
    public boolean hasCustomInstantiateInstance() {
      return true;
    }

    @Override
    public FunctionDTO instantiateInstance(SerializationStreamReader streamReader)
        throws SerializationException {
      return instantiate(streamReader);
    }

    @SuppressWarnings("unchecked") // the cast to List<String> is the problem here
    public static FunctionDTO instantiate(SerializationStreamReader streamReader)
        throws SerializationException {
      return new FunctionDTO(/* isDimension */ streamReader.readBoolean(),
          /* function name */ streamReader.readString(), /* source type name */ streamReader
              .readString(), /* return type name */ streamReader.readString(),
          /* parameter type names */ (List<String>) streamReader.readObject(),
          /* display name */ streamReader.readString(), /* ordinal */ streamReader.readInt());
    }

    @Override
    public void deserializeInstance(SerializationStreamReader streamReader, FunctionDTO instance)
        throws SerializationException {
      deserialize(streamReader, instance);
    }

    public static void deserialize(SerializationStreamReader streamReader, FunctionDTO instance) {
      // Done by instantiateInstance
    }
  }

  /**
   * Not serializable.
   */
  interface A extends IsSerializable {
  }

  /**
   * Not serializable due to Object field.
   */
  class C extends B {
    Object field;
  }

  /**
   * Not instantiable either, due to non-default constructor and final field
   */
  class D implements A {
    private final int i;

    public D(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Not instantiable; see {@link D}
   */
  class E implements A {
    private final int i;

    public E(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Not instantiable; see {@link D}
   */
  class F implements A {
    private final int i;

    public F(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Not instantiable; see {@link D}
   */
  class G implements A {
    private final int i;

    public G() {
      i = 0;
    }

    public G(int i) {
      super();
      this.i = i;
    }

    public int getI() {
      return i;
    }
  }

  /**
   * Auto serializable, but with back-reference to A for which the question of instantiable subtypes
   * depends on the serializability of this class; all other sub-classes are not instantiable.
   */
  class B implements A {
    private A a;

    public A getA() {
      return a;
    }

    public void setA(A a) {
      this.a = a;
    }
  }

  A getA();

  StoredDataMiningReportDTO getStoredDataMiningReportDTO();
}
