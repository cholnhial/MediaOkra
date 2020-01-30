#!/usr/bin/python

''' Gtime is a simple console application
    that retrieves the date of a location
    using an API it uses the location which
    can be a name of country/postcode/city..etc.
    it then uses this to query the time from the API.
'''

import xml.etree.ElementTree as ET
import urllib.request
import sys

class Gtime:
	'Gtime main class'
	APIURL = "http://api.worldweatheronline.com/free/v1/tz.ashx?key=%s&q=%s&format=xml"
	def __init__(self, apiKey, searchQuery):
		self.searchQuery = searchQuery
		self.localTime = ""
		self.location = ""
		self.locationType = ""
		self.apiKey = apiKey

	def getSearchQuery(self):
		return self.searchQuery

	def setSearchQuery(self, searchQuery):
		self.searchQuery = searchQuery

	def getApiKey(self):
		return self.apiKey

	def setApiKey(self, apiKey):
		self.apiKey = apiKey

	def getLocalTime(self):
		return self.localTime

	def getLocation(self):
		return self.location

	def executeQuery(self):

		# Make the query to get the XML
		formatedUrl = (Gtime.APIURL % (self.apiKey, self.searchQuery))

		# Finally request the XML
		response = urllib.request.urlopen(formatedUrl)
		xmlData = response.read()
		
		# Parse the XML data
		root = ET.fromstring(xmlData)
		
		# Check for error
		if root[0].tag == "error":
			return False
		#Everything is cool
		else:
			self.location = root[0][1].text
			self.localTime = root[1][0].text
		
		return True


# PROGRAM START

if len(sys.argv) != 2:
	print("Usage: %s <location|postcode|city|country>" % sys.argv[0])
	exit(1)

gtime = Gtime('d650e07040973887da54e3a7abfb424930575f97', sys.argv[1])
if gtime.executeQuery():
	print("\n")
	print('\033[1m' + '\033[92m' + gtime.getLocation() + '\033[0m')
	print('  \033[94m' + gtime.getLocalTime() + '\033[0m')
	print("\n")
	exit(0)
else:
	print("\033[91mSorry there was an error in processing your request" + '\033[0m')
	exit(1)
# PROGRAM END