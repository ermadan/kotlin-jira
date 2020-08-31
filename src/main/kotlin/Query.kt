import khttp.get
import khttp.structures.authorization.BasicAuthorization


fun main(args: Array<String>) {
    val jira = "https://myjira.com/jira02/rest/api/2/issue/"
    val auth = BasicAuthorization("danila.ermakov@mycompany.com", "XXXX")
    val project = "RMIB"
    println(
        get("${jira}BREXITCD-236", auth = auth).jsonObject
    )
    println(
        get("${jira}search",
            auth = auth,
            params = mapOf("jql" to """ 
                project = $project 
                and 'Epic Name' = 'some long name'""".trimMargin())).jsonObject
    )
}