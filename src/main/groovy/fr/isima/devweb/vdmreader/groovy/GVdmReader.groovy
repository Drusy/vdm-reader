/**
 * The MIT License
 *
 * Copyright (c) 2012, Daniel Petisme
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package fr.isima.devweb.vdmreader.groovy

import groovy.util.slurpersupport.NodeChild
import java.text.SimpleDateFormat
import java.util.concurrent.Executors
import java.util.logging.*
import static java.util.concurrent.TimeUnit.SECONDS
/**
 * @author Daniel Petisme
 * @version 1.0
 */
def LOGGER = Logger.getLogger("GVdmReader")
//Global Map
def VDMS = [:]
//The output file
def out = new File("vdm.out")

//POGO
class Vdm {
    String date
    String author
    String content

    def getAuthor() {
        author.toUpperCase()
    }
}

//Convenient method to format the VDM date (NB: Added as a static method of the class java.util.Date)
Date.metaClass.static.formatVDMDate = {
    new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH).parse(it as String).format("dd-MM-YYY HH:mm")
}

//Compute a key based on the date formated + the author name
NodeChild.metaClass.key = {"${Date.formatVDMDate(delegate.updated)}-${delegate.author}"}

//A classic Runnable to do the polling in a seperate Thread
def pollerRunner = new Runnable() {
    public void run() {
        LOGGER.log(Level.INFO, "Polling VDM")
        //Get all the entries of the VDM RSS feed
        //Keep only the new ones
        //compute the map [key:Vdm] of all the new stories
        //Add this map to the exiting VDM list
        VDMS += new XmlSlurper().parse("http://feeds.uri.lv/viedemerde").entry.findAll {
            !VDMS[it.key()]
        }.inject([:]) { map, entry ->
            // I use duck typing instead of using explicitly the "new" operator
            //Duck typing use the "as" keyword : myVar as aType
            map."${entry.key()}" = [date: Date.formatVDMDate(entry.updated), author: entry.author, content: entry.content] as Vdm
            map
        }
        LOGGER.log(Level.INFO, "Grand Total of VDMs: ${VDMS.size()}")
    }
};

def randomVDM = {
    LOGGER.log(Level.INFO, "Randoming a VDM")
    // *. is the spread operator it's equivalent to .collect {it.key}
    //Here I ask to get the key property for every entry present in the VDM map
    def key = VDMS*.key[new Random().nextInt(VDMS.size() + 1)]
    def vdm = VDMS[key]
    def text = """----------
  ${vdm.date}
  ${vdm.content}
          ${vdm.author}
----------
"""
    println text
    //Writting in the output file
    out << text
}

//Creating a thread which will invoke the poller immediately and then every 5 seconds
Executors.newScheduledThreadPool(1).scheduleAtFixedRate(pollerRunner, 0, 5, SECONDS)
//A map + Duck typing can be a convenient way to replay an anaonymous class :)
def prompterRunner = [run: randomVDM] as Runnable
Executors.newScheduledThreadPool(1).scheduleAtFixedRate(prompterRunner, 10, 10, SECONDS)