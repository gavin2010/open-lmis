package org.openlmis.report.service;

import lombok.NoArgsConstructor;
import org.apache.ibatis.session.RowBounds;
import org.openlmis.report.mapper.StockImbalanceReportMapper;
import org.openlmis.report.model.ReportData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * User: Wolde
 * Date: 7/27/13
 * Time: 4:45 PM
 */
@Service
@NoArgsConstructor
public class StockImbalanceReportDataProvider extends ReportDataProvider {

    private StockImbalanceReportMapper reportMapper;

    @Autowired
    public StockImbalanceReportDataProvider(StockImbalanceReportMapper mapper) {
        this.reportMapper = mapper;
    }

    @Override
    protected List<? extends ReportData> getBeanCollectionReportData(Map<String, String[]> filterCriteria) {
        RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET,RowBounds.NO_ROW_LIMIT);
        return reportMapper.getReport(filterCriteria, rowBounds);
    }

    @Override
    protected List<? extends ReportData> getResultSetReportData(Map<String, String[]> params) {
        return null;
    }

    @Override
    public List<? extends ReportData> getReportDataByFilterCriteriaAndPagingAndSorting(Map<String, String[]> filterCriteria, Map<String, String[]> sorter, int page, int pageSize) {
        RowBounds rowBounds = new RowBounds((page-1) * pageSize,pageSize);
        return reportMapper.getReport(filterCriteria, rowBounds);
    }

    @Override
    public int getReportDataCountByFilterCriteria(Map<String, String[]> filter) {
        return 0;
    }

    @Override
    public ReportData getReportFilterData(Map<String, String[]> params) {
        String facilityTypeId =  params.get("facilityTypeId")[0];
        String rgroupId =     params.get("rgroupId")[0];
        String facilityType =  "";
        String rgroup = "";

        if(facilityTypeId != null && !facilityTypeId.isEmpty()){
            if(facilityTypeId.equals("-1"))
                facilityType = "All Facility Types";
            else
                facilityType = params.get("facilityType")[0];
        }

        if(rgroupId != null && !rgroupId.isEmpty()){
            if(rgroupId.equals("-1"))
                rgroup = "All Reporting Groups";
            else
                rgroup = params.get("rgroup")[0];
        }
        final String finalFacilityType = facilityType;
        final String finalRgroup = rgroup;

        return new ReportData() {
            @Override
            public String toString() {
                return finalFacilityType +"\n"+ finalRgroup +"\n";
            }
        };
    }
}
