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

            // Create Child Nodes and Edges
            nodes.forEachIndexed { index, node ->
                val nodeId = "node_$index"

                // Child Node (Packing the description here so JS can see it!)
                val childNode = JSONObject().apply {
                    put("data", JSONObject().apply {
                        put("id", nodeId)
                        put("label", node.title)
                        put("description", node.description)
                    })
                }
                elementsArray.put(childNode)

                // Edge connecting Root to Child
                val edge = JSONObject().apply {
                    put("data", JSONObject().apply {
                        put("id", "edge_$index")
                        put("source", "root")
                        put("target", nodeId)
                    })
                }
                elementsArray.put(edge)
            }

            // 3. Send the final JSON string to the View to be injected
            view.injectGraphData(elementsArray.toString())
        }
    }

    override fun onNodeTapped(title: String, description: String) {
        view.updateNodeDetailsCard(title, description)
    }
}