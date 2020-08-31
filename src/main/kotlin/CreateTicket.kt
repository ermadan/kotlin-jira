import com.opencsv.CSVReaderHeaderAware
import com.opencsv.RFC4180Parser
import khttp.get
import khttp.post
import khttp.structures.authorization.BasicAuthorization
import org.json.JSONArray
import java.io.FileInputStream
import java.io.InputStreamReader
//Project is reading csv file with tasks
// CSV file example
//
//Project,Summary,Issue Type,Epic Link,Application Name,Labels
//Records Management,Confirm and Signoff retention strategy,EPIC12,APP1,"LABEL1, LABEL2"
//Records Management,Legal & Tax Holds check & modify/confirm deletion script,EPIC12,APP1,"LABEL1, LABEL2"
//Records Management,complete testing / tech build for disposal artefacts,Task,EPIC12,"LABEL1, LABEL2"
//Records Management,Semi automated disposals where required / Release tech build in to Prod,EPIC12,APP1,"LABEL1, LABEL2"


fun main(args: Array<String>) {
    val jira = "https://my.com/jira02/rest/api/2/"
    val issue = jira + "issue/"
    val auth = BasicAuthorization("danila.ermakov@my.com", "XXXXX")
    val project = "RMIB"

    val tasks = CSVReaderHeaderAware(
        InputStreamReader(FileInputStream("c:/temp/stories2.csv")),
        0, RFC4180Parser(), false, false,
        0, null)
    var task = tasks.readMap()
    while (task != null) {
        val epics = get("${jira}search",
            auth = auth,
            params = mapOf("jql" to """
                    project = $project 
                    and 'Epic Name' ~ '${task.get("Epic Link")}'""")).jsonObject

        val epicKey = (epics.get("issues") as JSONArray)
            .getJSONObject(0).get("key")

        val fields = mapOf(
                    "project" to mapOf("key" to project),
                    "summary" to task.get("Summary"),
                    "customfield_11900" to epicKey,
                    "issuetype" to mapOf("name" to "Task"),
                    "labels" to task.get("Labels")?.split(", ")?.toList()
        )
        val ticket = post(issue,
            auth = auth,
            json = mapOf("fields" to fields)).jsonObject
        println("story created " + ticket.get("key"))
        task = tasks.readMap()
    }
}