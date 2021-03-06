/*
 *
 *  Copyright (c) 2019-2021, NVIDIA CORPORATION.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package ai.rapids.cudf;

/**
 * Settings for writing Parquet files.
 */
public class ParquetWriterOptions extends CompressedMetadataWriterOptions {
  public enum StatisticsFrequency {
    /** Do not generate statistics */
    NONE(0),

    /** Generate column statistics for each rowgroup */
    ROWGROUP(1),

    /** Generate column statistics for each page */
    PAGE(2);

    final int nativeId;

    StatisticsFrequency(int nativeId) {
      this.nativeId = nativeId;
    }
  }

  public static class Builder extends CMWriterBuilder<Builder> {
    private StatisticsFrequency statsGranularity = StatisticsFrequency.ROWGROUP;
    private boolean isTimestampTypeInt96 = false;
    private int[] precisionValues = null;

    public Builder withStatisticsFrequency(StatisticsFrequency statsGranularity) {
      this.statsGranularity = statsGranularity;
      return this;
    }

    /**
     * Set whether the timestamps should be written in INT96
     */
    public Builder withTimestampInt96(boolean int96) {
      this.isTimestampTypeInt96 = int96;
      return this;
    }

    /**
     * This is a temporary hack to make things work.  This API will go away once we can update the
     * parquet APIs properly.
     * @param precisionValues a value for each column, non-decimal columns are ignored.
     * @return this for chaining.
     */
    public Builder withDecimalPrecisions(int ... precisionValues) {
      this.precisionValues = precisionValues;
      return this;
    }

    public ParquetWriterOptions build() {
      return new ParquetWriterOptions(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  private final StatisticsFrequency statsGranularity;

  private ParquetWriterOptions(Builder builder) {
    super(builder);
    this.statsGranularity = builder.statsGranularity;
    this.isTimestampTypeInt96 = builder.isTimestampTypeInt96;
    this.precisions = builder.precisionValues;
  }

  public StatisticsFrequency getStatisticsFrequency() {
    return statsGranularity;
  }

  /**
   * Return the flattened list of precisions if set otherwise empty array will be returned.
   * For a definition of what `flattened` means please look at {@link Builder#withDecimalPrecisions}
   */
  public int[] getPrecisions() {
    return precisions;
  }

  /**
   * Returns true if the writer is expected to write timestamps in INT96
   */
  public boolean isTimestampTypeInt96() {
    return isTimestampTypeInt96;
  }

  private boolean isTimestampTypeInt96;

  private int[] precisions;
}
