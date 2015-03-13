#!/usr/bin/env python

from cdb.common.exceptions.invalidRequest import InvalidRequest


class ErrorChecker:
    
    @classmethod
    def pdmLinkDrawingValidExtension(self, drawingNumber):
        result = True 
        #no extension was provided
        if drawingNumber.find('.') == -1:
            result = False
        
        #get the extension or last string after 
        extension = drawingNumber.split('.')[-1]
        #check if the extension is a valid length of 3 
        if extension.count('') != 4:
            result = False 
        
        if result == False:
            raise InvalidRequest('A PDMLink drawing number must have a valid exension. ex: drw, prt, asm, etc.')
        
        return result 
    