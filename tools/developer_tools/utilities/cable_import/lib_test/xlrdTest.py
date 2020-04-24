import xlrd
import xlwt

book = xlrd.open_workbook("/home/craig/projects/20200407-preimport-scripts/cable-specs-IT.xlsx")
sheet = book.sheet_by_index(0)
print(sheet.name)
print(sheet.nrows)
print(sheet.ncols)
for row_index in range (sheet.nrows):
    for col_index in range(sheet.ncols):
        print("%s - %s" % (xlrd.cellname(row_index, col_index), sheet.cell(row_index, col_index).value))

out_book = xlwt.Workbook()
out_sheet = out_book.add_sheet("Sheet")

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

out_book.save("/home/craig/projects/20200407-preimport-scripts/xlwt-test.xlsx")
