#!/usr/bin/env python

from cdb.common.db.api.userDbApi import UserDbApi


class ArgsGenerator:


    def __init__(self):
        self.usersDbApi = UserDbApi()
        self.usersDict = {}
        self.groupDict = {}

    def getNameDescriptionArgs(self, cdbObject, addionalArg = None):
        cdbObject = cdbObject.data
        name = cdbObject['name']
        description = cdbObject['description']
        if addionalArg is None:
            return (name, description)
        return (name, description, addionalArg)

    def getEntityInfoArgs(self, entityInfoData):
        createdByUserName = entityInfoData['createdByUserInfo'].data['username']
        createdByUserId = self.getUserId(createdByUserName)

        ownerUserInfo = entityInfoData['ownerUserInfo']
        ownerUserId = None
        if ownerUserInfo is not None:
            ownerUserName = ownerUserInfo.data['username']
            ownerUserId = self.getUserId(ownerUserName)

        ownerGroupInfo = entityInfoData['ownerUserGroup']
        ownerGroupId = None
        if ownerGroupInfo is not None:
            ownerGroupName = ownerGroupInfo.data['name']
            ownerGroupId = self.getGroupId(ownerGroupName)

        isGroupWriteable = entityInfoData['isGroupWriteable']
        createdOnDataTime = entityInfoData['createdOnDateTime']
        lastModifiedOnDateTime = entityInfoData['lastModifiedOnDateTime']

        return (createdByUserId, ownerUserId, ownerGroupId, isGroupWriteable, createdOnDataTime, lastModifiedOnDateTime)

    def getLogArgs(self, domainEntityLog):
        log = domainEntityLog.log
        text = log.text
        enteredByUserName = log.enteredBy.username
        enteredByUserId = self.getUserId(enteredByUserName)
        enteredOnDateTime = log.entered_on_date_time
        logTopic = log.logTopic
        logTopicName = None
        if logTopic is not None:
            logTopicName = logTopic.name

        return (text, enteredByUserId, None, None, logTopicName, enteredOnDateTime)

    def getGroupId(self, groupName):
        if groupName not in self.groupDict:
            groupId = self.usersDbApi.getUserGroupByName(groupName).data['id']
            self.groupDict[groupName] = groupId
        return self.groupDict[groupName]

    def getUserId(self, userName):
        if userName not in self.groupDict:
            userId = self.usersDbApi.getUserByUsername(userName).data['id']
            self.groupDict[userName] = userId
        return self.groupDict[userName]
