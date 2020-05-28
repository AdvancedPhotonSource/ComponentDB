import xlsxwriter

workbook = xlsxwriter.Workbook("/home/craig/projects/20200407-preimport-scripts/xlsxwriter-test.xlsx")
out_sheet = workbook.add_worksheet()

# header row
out_sheet.write(0, 0, "Name")
out_sheet.write(0, 1, "Description")
out_sheet.write(0, 2, "Contact Info")
out_sheet.write(0, 3, "URL")

# data row
out_sheet.write(1, 0, "CommScope")
# out_sheet.write(1, 1, "")
# out_sheet.write(1, 2, "")
# out_sheet.write(1, 3, "")

workbook.close()
