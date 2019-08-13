#!/usr/bin/env python

from cdbObject import CdbObject
from cdb.common.exceptions.invalidArgument import InvalidArgument

class PdmLinkDrawing(CdbObject):

    DEFAULT_KEY_LIST = ['number','name', 'windchillUrl', 'respEng', 'drafter', 'wbsDescription', 'title1',
                        'title2', 'title3', 'title4', 'title5', 'revisionList']
    VALID_EXTENSION_LIST = ['drw', 'prt', 'asm', 'sec', 'frm', 'pdf']

    def __init__(self, dict):
        CdbObject.__init__(self, dict)

    # Check drawing name
    @classmethod
    def checkDrawingNumber(cls, drawingNumber):
        if drawingNumber.count('.') != 1 or drawingNumber.split('.')[-1].lower() not in cls.VALID_EXTENSION_LIST:
            raise InvalidArgument('PDMLink drawing name must have one of the following extensions: %s' % cls.VALID_EXTENSION_LIST)

