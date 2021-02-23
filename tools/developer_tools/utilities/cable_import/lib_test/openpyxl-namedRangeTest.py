from openpyxl import load_workbook
wb = load_workbook("/home/craig/projects/20200406-import-data-collection-workbook/MBA Kabel Information_Tech.xlsx")
print(wb.get_named_ranges())
for defined_name in wb.get_named_ranges():
    print("%s: %s" % (defined_name.name, defined_name.value))