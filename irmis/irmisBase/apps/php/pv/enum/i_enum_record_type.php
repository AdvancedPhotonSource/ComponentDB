<?php     
/*
 * Defines business enumeration RecordTypeEnum which contains the set of known
 * EPICS record types using the traditional lowercase naming.
 * 
 */

class RecordTypeEnum {
    var $recordTypes;

    function RecordTypeEnum() {
        $this->recordTypes = array();
        $this->recordTypes[] = "ab1771IFE";
        $this->recordTypes[] = "ab1771IX";
        $this->recordTypes[] = "ab1771N";
        $this->recordTypes[] = "ab1791";
        $this->recordTypes[] = "abDcm";
        $this->recordTypes[] = "ai";

        $this->recordTypes[] = "ao";
        $this->recordTypes[] = "beamh";
        $this->recordTypes[] = "beamhchan";
        $this->recordTypes[] = "bi";
        $this->recordTypes[] = "bo";
        $this->recordTypes[] = "bpm";
        $this->recordTypes[] = "calc";
        $this->recordTypes[] = "calcout";
        $this->recordTypes[] = "cm";

        $this->recordTypes[] = "compress";
        $this->recordTypes[] = "datalog";
        $this->recordTypes[] = "ddlypulse";
        $this->recordTypes[] = "ddlypulsevme";
        $this->recordTypes[] = "dfanout";
        $this->recordTypes[] = "digitel";
        $this->recordTypes[] = "eg";
        $this->recordTypes[] = "egevent";
        $this->recordTypes[] = "er";

        $this->recordTypes[] = "erevent";
        $this->recordTypes[] = "event";
        $this->recordTypes[] = "fanout";
        $this->recordTypes[] = "fbuffer";
        $this->recordTypes[] = "gp307";
        $this->recordTypes[] = "gpib";
        $this->recordTypes[] = "histogram";
        $this->recordTypes[] = "image";
        $this->recordTypes[] = "iq";
        $this->recordTypes[] = "longin";
        $this->recordTypes[] = "longout";
        $this->recordTypes[] = "mbbi";
        $this->recordTypes[] = "mbbiDirect";
        $this->recordTypes[] = "mbbo";

        $this->recordTypes[] = "mbboDirect";
        $this->recordTypes[] = "memscan";
        $this->recordTypes[] = "motor";
        $this->recordTypes[] = "msbpm";
        $this->recordTypes[] = "msbpmX";
        $this->recordTypes[] = "pulseDelay";
        $this->recordTypes[] = "pulseTrain";
        $this->recordTypes[] = "rf";
        $this->recordTypes[] = "rt";

        $this->recordTypes[] = "runcontrol";
        $this->recordTypes[] = "satRga";
        $this->recordTypes[] = "scalcout";
        $this->recordTypes[] = "scaler";
        $this->recordTypes[] = "scan";
        $this->recordTypes[] = "sddsLaunch";
        $this->recordTypes[] = "sddsLaunch2";
        $this->recordTypes[] = "sddsLaunch3";
        $this->recordTypes[] = "sel";

        $this->recordTypes[] = "seq";
        $this->recordTypes[] = "serial";
        $this->recordTypes[] = "status";
        $this->recordTypes[] = "stringin";
        $this->recordTypes[] = "stringout";
        $this->recordTypes[] = "sub";
        $this->recordTypes[] = "subArray";
        $this->recordTypes[] = "timer";
        $this->recordTypes[] = "transform";

        $this->recordTypes[] = "vacScan";
        $this->recordTypes[] = "wait";
        $this->recordTypes[] = "waveform";
    }

    function find($idx) {
        return $this->recordTypes[$idx];
    }

    function length() {
        return count($this->recordTypes);
    }

}

?>
