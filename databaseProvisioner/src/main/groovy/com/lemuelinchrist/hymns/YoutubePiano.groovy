package com.lemuelinchrist.hymns

import com.lemuelinchrist.hymns.lib.Dao
import com.lemuelinchrist.hymns.lib.beans.HymnsEntity
import com.lemuelinchrist.hymns.lib.beans.TuneEntity

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *
 * @author Lemuel Cantos
 * @since 2020*
 */
class YoutubePiano {

    private Dao dao = new Dao()


    static void main(String[] args) {
        def youtubePiano = new YoutubePiano();
        youtubePiano.provision();
        println "end!!!!!"
    }

    void provision() {
        def pianoHymns = new File(this.getClass().getResource("/pianoHymns.txt").getPath());
        def iterator = pianoHymns.iterator();
        String line;
        def processedHymns = []

        while (iterator.hasNext()) {
            line = iterator.next().trim()
            Pattern pattern = Pattern.compile('.*?href="(.*?)".*?noopener">(.*?)</a></span>(.*)');
            Matcher matcher = pattern.matcher(line);
            if (matcher.find())
            {
                def title = matcher.group(3).trim()
                def number = matcher.group(2).trim()
                def link = matcher.group(1).trim()

                def splitStr = link.split("\\/")
                def code = splitStr[splitStr.length-1].trim().split('&amp;')[0].split("\\?")[0]

                if (!title.contains('(')) title="1st tune"
                splitStr = title.split("\\(")
                def comment = splitStr[splitStr.length-1].trim().split("\\)")[0].trim().replace("&nbsp;"," ")



                println "**************************************"
                println "hymn =" + number
                println "title = " + comment
                println "href = " + code

                if(!number.isNumber()) {
                    throw new Exception("hymn number invalid")
                }

                HymnsEntity hymn = dao.find("E"+number)
                if(hymn==null) {
                    throw new Exception("hymn not found")
                }
                TuneEntity tune = new TuneEntity();
                tune.id=hymn.id
                tune.comment=comment
                tune.youtubeLink=code

                println tune
                processedHymns+=hymn.id
                try {
                    dao.save(tune)
                } catch (Exception e) {
                    // do nothing
                }

            }
        }

    }
}
