#!/usr/bin/python

# This script was basically found on stackoverflow and slightly modified

import Quartz
import sys

# NSEvent.h
NSSystemDefined = 14

# hidsystem/ev_keymap.h
NX_KEYTYPE_SOUND_UP = 0
NX_KEYTYPE_SOUND_DOWN = 1
NX_KEYTYPE_PLAY = 16
NX_KEYTYPE_NEXT = 17
NX_KEYTYPE_PREVIOUS = 18
NX_KEYTYPE_FAST = 19
NX_KEYTYPE_REWIND = 20

def HIDPostAuxKey(key):
    def doKey(down):
        ev = Quartz.NSEvent.otherEventWithType_location_modifierFlags_timestamp_windowNumber_context_subtype_data1_data2_(
            NSSystemDefined, # type
            (0,0), # location
            0xa00 if down else 0xb00, # flags
            0, # timestamp
            0, # window
            0, # ctx
            8, # subtype
            (key << 16) | ((0xa if down else 0xb) << 8), # data1
            -1 # data2
            )
        cev = ev.CGEvent()
        Quartz.CGEventPost(0, cev)
    doKey(True)
    doKey(False)

if(sys.argv[1] == 'play' or sys.argv[1] == 'pause'):
	HIDPostAuxKey(NX_KEYTYPE_PLAY)
