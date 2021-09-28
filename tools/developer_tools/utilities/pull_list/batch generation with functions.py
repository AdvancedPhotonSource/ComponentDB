
import csv
import os
import re


filepath = r"C:\Users\mvirgo\Documents\Projects\APS-U\python_testing"
input_file_name = "cableDesignList.csv"

#
# GLOBAL VARIABLES/PARAMETERS
#
magnet_regex_str = "S(\d{2})([AB]):(M|Q|S|FC|FH|FV|SQ|H|V)(\d{1})(T?)"
Magnet_RegEx = re.compile(magnet_regex_str + '$')
Magnet_TC_RegEx = re.compile(magnet_regex_str + ":TC(\d{1})" + '$')
Magnet_Klixon_RegEx = re.compile(magnet_regex_str + ":TS(\d{1})" + '$')

UNIPOLAR_MAGNET_POWER_CABLE_TYPES = {"DLO 444 (pair)","DLO 535 (pair)","DLO #2 (pair)","DLO 4/0 (pair)"}
BIPOLAR_MAGNET_POWER_CABLE_TYPE = "#14/2c (corrector)"

class CableProperties:
    def __init__(self, import_id, owner, description, from_end, to_end, cable_type):
        self.id = import_id
        self.import_id = self.id
        self.owner = owner
        self.description = description
        self.from_end = from_end
        self.to_end = to_end
        self.cable_type = cable_type

def unipolar_magnet_power_cable_list_maker(cable):
    batch_suffix = "UPOW"

    my_match = Magnet_RegEx.match(cable.to_end)
    if my_match and cable.cable_type in UNIPOLAR_MAGNET_POWER_CABLE_TYPES:
        sector, section, magnet_type, magnet_number, trim = my_match.groups()
        cable_id = 'S' + sector + section + '-' + magnet_type + magnet_number + trim + '-' + batch_suffix
        return [cable.import_id, cable_id]
    return []

def bipolar_magnet_power_cable_list_maker(cable):
    batch_suffix = "BPOW"
     
    my_match = Magnet_RegEx.match(cable.to_end)
    if my_match and cable.cable_type == BIPOLAR_MAGNET_POWER_CABLE_TYPE:
        sector, section, magnet_type, magnet_number, trim = my_match.groups()
        cable_id = 'S' + sector + section + '-' + magnet_type + magnet_number + trim + '-' + batch_suffix
        return [cable.import_id, cable_id]
    return [] 
    
def magnet_thermocouple_cable_list_maker(cable):
    batch_suffix = "MAGTC"
     
    my_match = Magnet_TC_RegEx.match(cable.to_end)
    if my_match:
        sector, section, magnet_type, magnet_number, trim, tc_number = my_match.groups()
        cable_id = 'S' + sector + section + '-' + magnet_type + magnet_number + trim + '_TC' + tc_number + '-' + batch_suffix
        return [cable.import_id, cable_id]
    return []
    
def klixon_cable_list_maker(cable):
    batch_suffix = "KLIX"
     
    my_match = Magnet_Klixon_RegEx.match(cable.to_end)
    if my_match:
        sector, section, magnet_type, magnet_number, trim, klix_number = my_match.groups()
        cable_id = 'S' + sector + section + '-' + magnet_type + magnet_number + trim + '_TS' + klix_number + '-' + batch_suffix
        return [cable.import_id, cable_id]
    return [] 

# Convert from raw input list to cable entries list
def initialize(input_rows):
    # split the endpoints field into to's and from's, then put the list back together
    import_ids, owners, descriptions, endpoints, cable_types = zip(*input_rows)

    endpoints_split = [endpoint.split('|') for endpoint in endpoints]
    # this language feature is embarrasing , but that's not going to stop me from using it
    endpoints_split_stripped = [[s.strip() for s in line] for line in endpoints_split]
    from_ends, to_ends = zip(*endpoints_split_stripped)

    new_rows = zip(import_ids, owners, descriptions, from_ends, to_ends, cable_types)
    
    return [CableProperties(r[0], r[1], r[2], r[3], r[4], r[5]) for r in new_rows]

def batch_report_printer(this_batch, N_cable_entries, batch_name):

    print("Number of {} cables: {}".format(batch_name, len(this_batch)))
    for item in this_batch: print(4*' ' + item[0] + 2*' ' + item[1])
    print("Number remaining to process: {}\n".format(N_cable_entries)) 

# After finding a batch of cables in the cable list, delete the matched cables from the list
def expunge(cable_list, data_rows):
    
    for i in reversed(range(len(data_rows))):
        if cable_list[i]: del data_rows[i]
        

def main():
    
    os.chdir(filepath)

    with open(input_file_name, newline='') as f:
        reader = csv.reader(f)
        input_rows = list(reader)

    # get rid of the header line
    del input_rows[0] 

    cable_entries = initialize(input_rows)
   
    print("Number of cables to process: " + str(len(cable_entries)) + '\n')          
  
    # UNIPOLAR MAGNET POWER SUPPLY CABLES                            
    this_cable_list = [unipolar_magnet_power_cable_list_maker(cable_entry) for cable_entry in cable_entries]    
    this_batch = list(filter(lambda b: True if b else False, this_cable_list))
    expunge(this_cable_list, cable_entries)
    batch_report_printer(this_batch, len(cable_entries), "unipolar magnet power cables")
    
    # BIPOLAR MAGNET POWER SUPPLY CABLES
    this_cable_list = [bipolar_magnet_power_cable_list_maker(cable_entry) for cable_entry in cable_entries]    
    this_batch = list(filter(lambda b: True if b else False, this_cable_list))
    expunge(this_cable_list, cable_entries)
    batch_report_printer(this_batch, len(cable_entries), "bipolar magnet power cables")
    
    # MAGNET THERMOCOUPLE CABLES
    this_cable_list = [magnet_thermocouple_cable_list_maker(cable_entry) for cable_entry in cable_entries]    
    this_batch = list(filter(lambda b: True if b else False, this_cable_list))
    expunge(this_cable_list, cable_entries)
    batch_report_printer(this_batch, len(cable_entries), "magnet thermocouple cables")
    
    # MAGNET TKLIXON CABLES
    this_cable_list = [klixon_cable_list_maker(cable_entry) for cable_entry in cable_entries]    
    this_batch = list(filter(lambda b: True if b else False, this_cable_list))
    expunge(this_cable_list, cable_entries)
    batch_report_printer(this_batch, len(cable_entries), "magnet klixon cables")

if __name__ == "__main__":
    main()





