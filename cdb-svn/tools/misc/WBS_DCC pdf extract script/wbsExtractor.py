#!/usr/bin/python
from pdfminer.pdfparser import PDFParser
from pdfminer.pdfdocument import PDFDocument
from pdfminer.pdfpage import PDFPage
from pdfminer.pdfpage import PDFTextExtractionNotAllowed
from pdfminer.pdfinterp import PDFResourceManager
from pdfminer.pdfinterp import PDFPageInterpreter
from pdfminer.layout import LAParams
from pdfminer.converter import PDFPageAggregator
import codecs



# Open a PDF file.
fp = open('WBS INDEX DICTIONARY_JULY 2013.pdf', 'rb')
# Create a PDF parser object associated with the file object.
parser = PDFParser(fp)
# Create a PDF document object that stores the document structure.
# Supply the password for initialization.
document = PDFDocument(parser)
# Check if the document allows text extraction. If not, abort.
if not document.is_extractable:
    raise PDFTextExtractionNotAllowed

# Set parameters for analysis.
laparams = LAParams()
# Create a PDF page aggregator object.
layoutrsrcmgr = PDFResourceManager()
layoutDevice = PDFPageAggregator(layoutrsrcmgr, laparams=laparams)
layoutInterperter = PDFPageInterpreter(layoutrsrcmgr, layoutDevice)
pages = PDFPage.create_pages(document)

class Wbs_dcc_object:
    def __init__(self, number,description):
        self.number = number
        self.description = description
    def getNumber(self):
        return self.number
    def getDescription(self):
        return self.description



wbs_dcc = []

for pageNum, page in enumerate(pages):
    layoutInterperter.process_page(page)
    # receive the LTPage object for the page.



    layout = layoutDevice.get_result()

    #Find WBS and description index
    wbsIndex = -1
    descriptionIndex = -1
    for i,pageElement in enumerate(layout._objs):
        if hasattr(pageElement, '_objs'):
            elements = pageElement._objs
            if(elements[0].get_text() == 'CODE\n'):
                if pageElement._objs.__len__ > 1:
                    del pageElement._objs[0]
                    wbsIndex = i
                else:
                    wbsIndex = i +1
                break
            if elements.__len__() > 1:
                checkText = elements[1].get_text().split(' ')[0]
                if(checkText == 'INDENTURE'):
                    descriptionIndex = i + 1

    print descriptionIndex
    if wbsIndex == -1:
        raise Exception('Could not find a valid wbsIndex')

    wbsList = layout._objs[wbsIndex]._objs
    descriptionList = layout._objs[descriptionIndex]._objs

    if wbsList == []:
        wbsList = layout._objs[wbsIndex+1]._objs

    validDescription = wbsList.__len__() == descriptionList.__len__()

    ctr = 0
    desciptionText = ''
    for i, wbsNum in enumerate(wbsList):
        ctr = ctr + 1
        text = wbsNum.get_text()[0:-1]
        if validDescription:
            desciptionText = descriptionList[i].get_text()[0:-1]
        duplicate = False
        for wbs in wbs_dcc:
            if text == wbs.getNumber():
                duplicate = True
                print 'duplicate'
        if not duplicate:
            wbs_dcc.append(Wbs_dcc_object(text, desciptionText))

    print 'Processed: ' + str(pageNum) + ' num wbs: ' + str(ctr)
    if not validDescription:
        print 'couldnt retrieve description'


for number in wbs_dcc:
    print number.getNumber()

print 'total wbs numbers: ' + str(wbs_dcc.__len__())


def create_sql_file(startingIndex,propertyIndex, numbers):
    with codecs.open('wbs_dcc_allowed_values.sql', 'a', encoding='utf8') as file:
        for i, number in enumerate(numbers):
            file.write('('+str(startingIndex)+','+str(propertyIndex)+",'"+number.getNumber()+"',NULL,'"+number.getDescription()+"',"+str(i+1)+'.00),\n')
            startingIndex += 1


create_sql_file(367,74, wbs_dcc)
