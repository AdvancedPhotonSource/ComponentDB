/*
   Copyright (c) 2004-2005 The University of Chicago, as Operator
   of Argonne National Laboratory.
*/
package gov.anl.aps.irmis.service.pv;

// java imports
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

// IRMIS persistence layer
import gov.anl.aps.irmis.persistence.pv.IOCBootDAO;
import gov.anl.aps.irmis.persistence.pv.IOCBoot;
import gov.anl.aps.irmis.persistence.pv.RecordTypeDAO;
import gov.anl.aps.irmis.persistence.pv.RecordType;
import gov.anl.aps.irmis.persistence.pv.RecordDAO;
import gov.anl.aps.irmis.persistence.pv.RecordClientDAO;
import gov.anl.aps.irmis.persistence.pv.Record;
import gov.anl.aps.irmis.persistence.pv.FieldDAO;
import gov.anl.aps.irmis.persistence.pv.Field;
import gov.anl.aps.irmis.persistence.pv.FieldType;
import gov.anl.aps.irmis.persistence.pv.IOCResource;
import gov.anl.aps.irmis.persistence.pv.IOCDAO;
import gov.anl.aps.irmis.persistence.pv.IOC;
import gov.anl.aps.irmis.persistence.pv.IOCStatus;
import gov.anl.aps.irmis.persistence.pv.IOCStatusDAO;
import gov.anl.aps.irmis.persistence.DAOException;

// IRMIS service layer
import gov.anl.aps.irmis.service.IRMISService;
import gov.anl.aps.irmis.service.IRMISException;

/**
 * Process Variable Service providing a variety of methods to acquire and
 * manipulate the PV object graph. The methods here are themselves purely
 * transactional, although some take existing object graphs as arguments.
 * In other words, you do not use new to make an instance of this class.
 *
 * An IRMISException is generally thrown when the supplied arguments are
 * not what is expected, or when there is some underlying database access
 * problem. 
 */
public class PVService extends IRMISService {

    // DO NOT PUT ANY INSTANCE VARIABLES HERE

    /**
     * Hide constructor, since all methods are static.
     */
    private PVService() {
    }

    /**
     * Find all current IOCBoot objects. By current we mean the currently
     * running set of ioc boots in the control system that have records
     * associated with them.
     *
     * @return list of <code>IOCBoot</code> objects
     * @throws IRMISException
     */
    public static List findCurrentIOCBootList() throws IRMISException {
        IOCBootDAO ibDAO = null;
        List bootList = null;
        try {
            ibDAO = new IOCBootDAO();
            bootList = ibDAO.findCurrentLoads();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
        return bootList;
    }

    /**
     * Find all IOCBoot objects for a given ioc, including past boots.
     *
     * @param ioc a new or persistent ioc data object
     * @return list of <code>IOCBoot</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findIOCBootsForIOC(IOC ioc) throws IRMISException {
        IOCBootDAO ibDAO = null;
        List bootList = null;
        try {
            ibDAO = new IOCBootDAO();
            bootList = ibDAO.findAllByIocName(ioc.getIocName());
        } catch (DAOException de) {
            throw new IRMISException(de);
        }
        return bootList;
    }

    /**
     * Find list of all known record types. The list of returned
     * <code>RecordType</code> objects is not useful for it's
     * associations with other objects, but rather as a master list
     * of all record type names found in the database. Typically used
     * to populate a selection list.
     *
     * @return list of <code>RecordType</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findRecordTypeList() throws IRMISException {
        RecordTypeDAO rtDAO = null;
        List rtList = null;
        try {
            rtDAO = new RecordTypeDAO();
            rtList = rtDAO.findAllRecordTypes();
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return rtList;
    }

    /**
     * Find the set of <code>Record</code> objects that meets the constraints given by
     * searchParams. From each record object you may navigate the associations to find
     * the set of fields, the record type, parent ioc boot, etc.
     *
     * @param searchParams search parameters object with constraints such as record name pattern
     * @return list of <code>Record</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findRecordList(PVSearchParameters searchParams) throws IRMISException {

        RecordDAO rDAO = null;
        List recordList = null;
        try {
            rDAO = new RecordDAO();
            // set up search constraints
            
            // ioc boot constraint
            if (searchParams.getIocBootList() != null) {
                Iterator it = searchParams.getIocBootList().iterator();
                while (it.hasNext()) {
                    IOCBoot ib = (IOCBoot)it.next();
                    rDAO.addBootIdConstraint(ib.getId());
                }
            }

            // record type constraint
            if (searchParams.getRecordTypeList() != null) {
                Iterator it = searchParams.getRecordTypeList().iterator();
                while (it.hasNext()) {
                    RecordType rt = (RecordType)it.next();
                    rDAO.addRecordTypeConstraint(rt.getRecordType());
                }
            }

            // ioc resource constraint
            if (searchParams.getIocResourceList() != null) {
                Iterator it = searchParams.getIocResourceList().iterator();
                while (it.hasNext()) {
                    IOCResource ir = (IOCResource)it.next();
                    rDAO.addFieldIocResourceIdConstraint(ir.getId());
                }
            }

            // wildcard string constraints
            rDAO.setRecordNameGlobConstraint(searchParams.getRecNameGlob());
            rDAO.setFieldNameGlobConstraint(searchParams.getFieldNameGlob());
            rDAO.setFieldValueGlobConstraint(searchParams.getFieldValueGlob());

            // do search
            recordList = rDAO.findByConstraints();

        } catch (DAOException de) {
            throw new IRMISException(de);
        }
        return recordList;
    }

    /**
     * Takes result from findRecordList, which may contain repeated instances
     * of a given record (from repeated ioc boots), and collapses the list into
     * a list of lists in which each element represents a single record, and the
     * sublist is the record over several ioc boots.
     *
     * @param recordList list of <code>Record</code> objects from findRecordList
     * @return list of lists, one element per record name
     * @throws IRMISException
     */
    public static List collapseRecordList(List recordList) throws IRMISException {
        List collapsedList = null;

        if (recordList != null) {
            HashMap uniqueRecordMap = new HashMap();
            Iterator recordIt = recordList.iterator();
            while (recordIt.hasNext()) {
                Record rec = (Record)recordIt.next();
                
                // add record to ArrayList of existing map entry
                if (uniqueRecordMap.containsKey(rec.getRecordName())) {
                    ArrayList chain = (ArrayList)uniqueRecordMap.get(rec.getRecordName());
                    chain.add(rec);
                    
                } else {  // create new ArrayList and map entry
                    ArrayList newChain = new ArrayList();
                    newChain.add(rec);
                    uniqueRecordMap.put(rec.getRecordName(),newChain);
                }
            }
            Iterator it = uniqueRecordMap.values().iterator();
            collapsedList = new ArrayList();
            while (it.hasNext()) {
                ArrayList sublist = (ArrayList)it.next();
                collapsedList.add(sublist);
            }
        }        
        return collapsedList;
    }

    /**
     * Finds the complete list of IOCBoot objects that have participated
     * in the lifecycle of a given process variable (Record). This begins
     * with the first ioc boot in which the record appears, followed by
     * a record of reboots of that ioc, and any other ioc's to which the
     * pv might have been relocated. If a Record has disappeared from the
     * current set of records, a final IOCBoot record will be included which
     * indicates the boot date at which the given record disappeared.
     *
     * @param record a <code>Record</code> object with at least the recordName set
     * @return list of <code>IOCBoot</code> objects, empty if none
     * @throws IRMISException
     */
    public static List findCompleteRecordBootHistory(Record record) throws IRMISException {
        boolean debug = false;
        List recBootHistoryList = new ArrayList();
        
        // find all ocurrences of this record over time
        PVSearchParameters pvSearchParams = new PVSearchParameters();
        pvSearchParams.setRecNameGlob(record.getRecordName());
        List recList = findRecordList(pvSearchParams);
        if (debug) System.out.println("recList size: "+recList.size());
        
        // find all ioc names involved in boots of the given record over time
        List iocList = new ArrayList();
        List iocFirstBootList = new ArrayList();
        Iterator recListIt = recList.iterator();
        while (recListIt.hasNext()) {
            Record rec = (Record)recListIt.next();
            IOC ioc = rec.getIocBoot().getIoc();
            if (!iocList.contains(ioc)) {
                iocList.add(ioc);
                iocFirstBootList.add(rec.getIocBoot());
                if (debug) System.out.println("add ioc "+ioc.getIocName()+" to iocList and iocFirstBootList");
            }
        }

        // Build up master list of all ioc boots pertaining to record. This may
        // be for more than one ioc (in case record is moved from one ioc to another).
        Iterator iocListIt = iocList.iterator();
        while (iocListIt.hasNext()) {
            IOC ioc = (IOC)iocListIt.next();
            List iocBoots = findIOCBootsForIOC(ioc);
            if (debug) System.out.println("for ioc "+ioc.getIocName()+" iocBoots list size: "+iocBoots.size());
            recBootHistoryList.addAll(iocBoots);
        }


        /* Check to see if there are any reboots after last in which record appeared.
           If so, this means the pv disappeared at that point. Mark this point forward
           by setting "participates" flag in IOCBoot to false. Remove elements before
           and after appearance of PV. This must be done for each ioc in which a pv
           appears, hence the outermost loop over the iocs.
        */
        IOCBoot firstIocBoot = null;
        IOCBoot lastIocBoot = null;
        Iterator recBootHistoryListIt = recBootHistoryList.iterator();
        if (debug) System.out.println("rec boot history list size: "+recBootHistoryList.size());
        IOC currentIoc = null;
        IOC previousIoc = null;
        boolean foundLast = false;
        boolean remove = true;
        
        while (recBootHistoryListIt.hasNext()) {
            IOCBoot iocBoot = (IOCBoot)recBootHistoryListIt.next();

            // if we hit a new ioc, reset our state
            if (currentIoc == null ||
                !currentIoc.equals(iocBoot.getIoc())) {
                if (debug) System.out.println("found new ioc: "+iocBoot.getIoc().getIocName()+" scanning recList for first and last boot");
                previousIoc = currentIoc;
                currentIoc = iocBoot.getIoc();
                // find first and last occurrence of record in boot sequence
                recListIt = recList.iterator();
                boolean firstHit = false;
                while (recListIt.hasNext()) {
                    Record rec = (Record)recListIt.next();
                    if (debug) System.out.println("recList rec boot id:"+rec.getIocBoot().toString()+" ioc:"+rec.getIocBoot().getIoc().getIocName());
                    if (!firstHit &&
                        rec.getIocBoot().getIoc() == currentIoc) {
                        firstHit = true;
                        if (debug) System.out.println("setting firstIocBoot to rec boot id");
                        firstIocBoot = lastIocBoot = rec.getIocBoot();
                    } else if (rec.getIocBoot().getIoc() == currentIoc) {
                        if (debug) System.out.println("setting lastIocBoot to rec boot id");
                        lastIocBoot = rec.getIocBoot();
                    }
                }
                foundLast = false;
                remove = true;
                if (debug) System.out.println("working through ioc "+currentIoc.getIocName());
            }

            // reset since cached object may have state changed from previous query
            iocBoot.setParticipates(true);  
            
            long stamp = 0;
            if (iocBoot.getIocBootDate() != null)
                stamp = iocBoot.getIocBootDate().getTime();
            if (debug) System.out.println("\nelement: "+iocBoot.toString()+" ioc: "+iocBoot.getIoc().getIocName()+" date:" + iocBoot.getIocBootDate() + " -- " + stamp);
            if (iocBoot.equals(lastIocBoot)) {
                if (debug) System.out.println("iocBoot equals lastIocBoot, set foundLast true");
                foundLast = true;
                if (iocBoot.equals(firstIocBoot)) {
                    if (debug) System.out.println("  iocBoot also equals firstIocBoot, set remove false");
                    remove = false;
                }
            } else if (iocBoot.equals(firstIocBoot)) {
                if (debug) System.out.println("iocBoot equals firstIocBoot, set remove false");
                remove = false;
            } else if (foundLast && !remove &&
                       (previousIoc == null || iocBoot.getIoc() == previousIoc) &&
                       (iocBoot.getIocResources() != null) &&
                       (iocBoot.getIocResources().size() > 0)) {
                
                if (debug) System.out.println("found iocBoot after last appearance, participates false remove true");
                iocBoot.setParticipates(false);
                remove = true;
            } else if (remove) {
                if (debug) System.out.println("remove element");
                recBootHistoryListIt.remove();
            }
        }  // end while recBootHistoryList

        // sort recBootHistory by db record modified date (can't count on boot date)
        Collections.sort(recBootHistoryList, new Comparator() {
                public int compare(Object o1, Object o2) throws ClassCastException {
                    IOCBoot boot1 = (IOCBoot)o1;
                    IOCBoot boot2 = (IOCBoot)o2;
                    return boot1.getModifiedDate().compareTo(boot2.getModifiedDate());
                }
                
            });
        return recBootHistoryList;
    }

    /**
     * Conduct search for fields of a given record. We don't conduct an explict
     * search here, but rather let lazy instantiation get the fields. We just need
     * to invoke a single call to record.getFields(). But since not all fields are
     * explicitly mentioned in a db file, we must also query the fields as defined
     * by the dbd file, and add those to the set returned by record.getFields() as 
     * well. It is here we determine the "state" of a field, the possible states
     * being: Field.DEFAULT, Field.OVERWRITTEN, Field.USER_DEFINED.
     *
     * @param record persistent record object for which to retrieve fields, modified in place
     *
     * @see Field
     */
    public static void findFieldsForRecord(Record record) {

        Record selectedRecord = record;
        SortedSet fields = null;
        SortedSet fieldTypes = null;

        // getFields will cause sql query to db
        fields = selectedRecord.getFields();
        
        // query db (implied) to get all field types defined for given record type
        fieldTypes = selectedRecord.getRecordType().getFieldTypes();

        // Iterate over field types, adding new fields as needed to
        // selectedRecord. Here is where we determine field state.
        Iterator rtftIt = fieldTypes.iterator();
        while (rtftIt.hasNext()) {
            FieldType rtft = (FieldType)rtftIt.next();
            boolean found = false;
            Iterator fIt = fields.iterator();
            while (fIt.hasNext()) {
                Field f = (Field)fIt.next();
                if (f.getFieldType().getFieldType()
                    .equals(rtft.getFieldType())) {
                    found = true;
                    if (f.getFieldState() == Field.UNKNOWN) {
                        if (rtft.getDefaultFieldValue().equals(f.getFieldValue())) {
                            f.setFieldState(Field.OVERWRITTEN);
                        } else {
                            f.setFieldState(Field.USER_DEFINED);
                        }
                    }
                    break;
                }
            }
            if (!found) {
                Field newField = new Field();
                newField.setFieldType(rtft);
                newField.setRecord(selectedRecord);
                newField.setFieldState(Field.DEFAULT);
                newField.setFieldValue(rtft.getDefaultFieldValue());
                // add to record, although this will not be persisted
                // beyond current session.
                selectedRecord.addField(newField);
            }
        }
        
    }

    /**
     * Find the list of record fields that contain a reference to the given record.
     * This is typically a field of type DBF_INLINK, DBF_OUTLINK, or DBF_FWDLINK.
     * The other records to be searched are found via the given iocBootList, which
     * is typically the "current" set of ioc boots (and hence the current set of 
     * records).
     *
     * @param iocBootList list of <code>IOCBoot</code> objects from which referring fields are found
     * @param record the record to which we are hoping to find references
     * @return list of <code>Field</code> objects, empty if none found
     * @throws IRMISException
     */
    public static List findReferringFieldsForRecord(List iocBootList, Record record) 
        throws IRMISException {

        FieldDAO fDAO = null;
        List fieldList = null;
        try {
            fDAO = new FieldDAO();
            fieldList = 
                fDAO.findByRecordNameAndLink(iocBootList,
                                             record.getRecordName());
        } catch (DAOException de) {
            throw new IRMISException(de);
        }                                
        return fieldList;
    }

    /**
     * Find the list of <code>RecordClient</code> that contain a reference to the
     * given record. This is a list of known applications (MEDM adl files, alarm handler
     * files, etc) that contain a reference to the given record.
     *
     * @param record the record to which we hope to find references
     * @return list of <code>RecordClient</code> objects
     * @throws IRMISException
     */
    public static List findReferringClientsForRecord(Record record) throws IRMISException {
        
        RecordClientDAO rcDAO = null;
        List recordClientList = null;
        try {
            rcDAO = new RecordClientDAO();
            recordClientList = rcDAO.findByRecordName(record.getRecordName());
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return recordClientList;
    }

    /**
     * Find list of all known ioc's currently scanned into the database.
     *
     * @return list of <code>IOC</code> objects
     * @throws IRMISException
     */
    public static List findIOCList() throws IRMISException {

        // Get list of IOC objects
        IOCDAO iocDAO = null;
        List iocList = null;
        try {
            // get list of ioc's
            iocDAO = new IOCDAO();
            iocList = iocDAO.findIOCs();

        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return iocList;
    }

    /**
     * Find list of all unique system names.
     *
     * @return list of strings
     * @throws IRMISException
     */
    public static List findSystems() throws IRMISException {
        // Get list of IOC objects
        IOCDAO iocDAO = null;
        List systemList = null;
        try {
            // get list of ioc's
            iocDAO = new IOCDAO();
            systemList = iocDAO.findSystems();

        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return systemList;
    }        

    /**
     * Find list of all ioc status.
     *
     * @return list of <code>IOCStatus</code> objects
     * @throws IRMISException
     */
    public static List findAllIOCStatus() throws IRMISException {
        // Get list of IOCStatus objects
        IOCStatusDAO iocStatusDAO = null;
        List statusList = null;
        try {
            // get list of ioc status'
            iocStatusDAO = new IOCStatusDAO();
            statusList = iocStatusDAO.findAllIOCStatus();

        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return statusList;
    }        

    /**
     * Find list of all known ioc's currently scanned into the database
     * that belong to the given system, such as "PAR" or "Booster".
     *
     * @param system system name, as in "PAR" or "Booster"
     * @return list of <code>IOC</code> objects
     * @throws IRMISException
     */
    public static List findIOCListBySystem(String system) throws IRMISException {
        // Get list of IOC objects
        IOCDAO iocDAO = null;
        List iocList = null;
        try {
            // get list of ioc's
            iocDAO = new IOCDAO();
            iocList = iocDAO.findIOCsBySystem(system);

        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
        return iocList;
    }

    /**
     * Save or update given ioc object. If ioc is a new <code>IOC</code> object,
     * then a new database row will be inserted. If ioc is an existing (but modified)
     * object, then the corresponding database row will be updated.
     *
     * @param ioc object to be persisted
     * @throws IRMISException
     */
    public static void saveIOC(IOC ioc) throws IRMISException {
        IOCDAO iocDAO = null;
        try {
            iocDAO = new IOCDAO();
            iocDAO.save(ioc);
        } catch (DAOException de) {
            throw new IRMISException(de);
        }        
    }

}
