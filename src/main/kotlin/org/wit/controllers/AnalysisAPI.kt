package org.wit.controllers
import io.javalin.http.Context
import org.wit.repository.AnalysisDAO

object AnalysisAPI {
    private val analysisDAO = AnalysisDAO()
    fun getAnalysisDetails(ctx: Context)
    {
        ctx.json(analysisDAO.getAll(ctx.pathParam("user-id").toInt()))
    }
}