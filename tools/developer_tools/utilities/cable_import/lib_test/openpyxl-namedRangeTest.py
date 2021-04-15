from openpyxl import load_workbook
wb = load_workbook("/home/craig/projects/20200406-import-data-collection-workbook/MBA Kabel Information_Tech.xlsx", data_only=True)
for defined_name in wb.defined_names.definedName:
    print("%s: %s" % (defined_name.name, defined_name.value))