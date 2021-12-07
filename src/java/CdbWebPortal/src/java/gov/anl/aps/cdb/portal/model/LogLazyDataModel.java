/*
 * Copyright (c) UChicago Argonne, LLC. All rights reserved.
 * See LICENSE file.
 */
package gov.anl.aps.cdb.portal.model;

import gov.anl.aps.cdb.portal.model.db.beans.LogFacade;
import gov.anl.aps.cdb.portal.model.db.beans.builder.LogQueryBuilder;
import gov.anl.aps.cdb.portal.model.db.entities.Log;
import gov.anl.aps.cdb.portal.model.db.entities.LogLevel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

/**
 *
 * @author darek
 */
public class LogLazyDataModel extends CdbLazyDataModel {

    private static final Logger LOGGER = LogManager.getLogger(LogLazyDataModel.class.getName());

    List<Log> logList;    
    Integer lastCount = 0; 
    
    List<LogLevel> filterViewSelectedLogLevels;
    
    LogFacade logFacade; 
    LogQueryBuilder logQueryBuilder; 

    public LogLazyDataModel(List<LogLevel> filterViewSelectedLogLevels) {
        this.logFacade = LogFacade.getInstance();      
        this.filterViewSelectedLogLevels = filterViewSelectedLogLevels;
    }

    protected void updateLogList(List<Log> logList) {
        this.logList = logList;
        setRowCount(logList.size());
    }

    @Override
    public List load(int first, int pageSize, Map sortOrderMap, Map filterBy) {
        String sortField = null;
        SortMeta sortMeta;
        SortOrder sortOrder = null;

        if (!sortOrderMap.isEmpty()) {
            sortField = (String) sortOrderMap.keySet().toArray()[0];
            sortMeta = (SortMeta) sortOrderMap.get(sortField);
            sortOrder = sortMeta.getOrder();
        }

        if (needToReloadLastQuery(sortOrderMap, filterBy, "Log")) {
            // Currently only works with system log level 
            if (filterViewSelectedLogLevels != null) {
                logQueryBuilder = new LogQueryBuilder(filterBy, sortField, sortOrder, filterViewSelectedLogLevels);               
                
                Long count = this.logFacade.getCountForQuery(logQueryBuilder);
                lastCount = count.intValue();
                setRowCount(lastCount);              
            }
        }
        
        if (logQueryBuilder != null) {
            logList = this.logFacade.findByDataTableFilterQueryBuilder(logQueryBuilder, first, pageSize);
        } else {
            logList = new ArrayList<>(); 
        }
        
        return logList;
    }    

    @Override
    public int count(Map map) {
        return lastCount;
    }

    @Override
    public String getRowKey(Object object) {
        if (object instanceof Log) {
            return ((Log) object).getViewUUID();
        }
        return "";
    }

}
