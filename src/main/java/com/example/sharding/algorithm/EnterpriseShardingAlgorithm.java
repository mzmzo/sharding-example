package com.example.sharding.algorithm;

import com.google.common.base.Preconditions;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import org.springframework.util.CollectionUtils;

import java.util.Collection;

/**
 * 企业分表算法
 *
 * @author User
 */
public class EnterpriseShardingAlgorithm implements PreciseShardingAlgorithm {

    @Override
    public String doSharding(Collection availableTargetNames, PreciseShardingValue shardingValue) {
        Preconditions.checkState("ese_id".equals(shardingValue.getColumnName()), "enterprise sharding algorithm only support enterprise id sharding.");
        return getActualTableName(availableTargetNames, shardingValue);
    }

    /**
     * 按企业id分表，若可用表中无该企业的表，则返回逻辑表名
     *
     * @param availableTargetNames available data sources or tables's names
     * @param shardingValue        sharding value
     * @return sharding result for data source or table's name
     */
    private String getActualTableName(Collection availableTargetNames, final PreciseShardingValue shardingValue) {

        String currentTableName = shardingValue.getLogicTableName() + "_" + shardingValue.getValue();

        if (!CollectionUtils.isEmpty(availableTargetNames)) {
            if (availableTargetNames.contains(currentTableName)) {
                return currentTableName;
            }
        }
        return shardingValue.getLogicTableName();
    }

}
