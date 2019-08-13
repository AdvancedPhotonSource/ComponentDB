#!/usr/bin/env python

from dbLegacy.entities.cdbDbEntity import CdbDbEntity
from dbLegacy.cdbObjects import componentInstanceLocationHistory


class ComponentInstanceLocationHistory(CdbDbEntity):

    mappedColumnDict = {
        'component_instance_id': 'componentInstanceId',
        'location_id': 'locationId',
        'location_details': 'locationDetails',
        'entered_on_date_time': 'enteredOnDateTime',
        'entered_by_user_id': 'enteredByUserId'
    }

    cdbObjectClass = componentInstanceLocationHistory.ComponentInstanceLocationHistory

    def __init__(self, **kwargs):
        CdbDbEntity.__init__(self, **kwargs)