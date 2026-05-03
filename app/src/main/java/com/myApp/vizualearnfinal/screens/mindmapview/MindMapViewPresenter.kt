package com.myApp.vizualearnfinal.screens.mindmapview

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MindMapViewPresenter(
    private val view: MindMapViewContract.View,
    private val model: MindMapViewModel
) : MindMapViewContract.Presenter {

    override fun loadMapData(mapId: Int, mapTitle: String) {
        if (mapId == -1) {
            view.showErrorAndFinish("Error loading Mind Map")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val nodes = model.getNodes(mapId)

            // 1. Update the headers
            view.setMapHeaders(mapTitle, "Biology • ${nodes.size} nodes • AI generated")

            // 2. Build the Cytoscape JSON Array
            val elementsArray = JSONArray()

            // Create Root Node
            val rootNode = JSONObject().apply {
                put("data", JSONObject().apply {
                    put("id", "root")
                    put("label", mapTitle)
                })
            }
            elementsArray.put(rootNode)

            nodes.forEach { node ->
                // WE REMOVED THE INTERCEPTION! Let Gemini's title shine.
                val nodeTitle = node.title

                elementsArray.put(JSONObject().apply {
                    put("data", JSONObject().apply {
                        put("id", node.nodeId)
                        put("label", nodeTitle)
                        put("description", node.description)
                    })
                })

                if (node.parentId != "null" && node.parentId.isNotEmpty() && node.nodeId != "root") {
                    elementsArray.put(JSONObject().apply {
                        put("data", JSONObject().apply {
                            put("id", "edge_${node.nodeId}")
                            put("source", node.parentId)
                            put("target", node.nodeId)
                        })
                    })
                }
            }
            val rawJsonString = elementsArray.toString()
            val base64Json = android.util.Base64.encodeToString(
                rawJsonString.toByteArray(Charsets.UTF_8),
                android.util.Base64.NO_WRAP
            )

            view.injectGraphData(base64Json)
        }
    }

    override fun onNodeTapped(title: String, description: String) {
        view.updateNodeDetailsCard(title, description)
    }
}