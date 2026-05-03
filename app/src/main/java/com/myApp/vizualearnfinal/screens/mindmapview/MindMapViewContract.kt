package com.myApp.vizualearnfinal.screens.mindmapview

interface MindMapViewContract {
    interface View {
        fun setMapHeaders(title: String, subtitle: String)
        fun injectGraphData(jsonString: String)
        fun updateNodeDetailsCard(title: String, description: String)
        fun showErrorAndFinish(message: String)
    }

    interface Presenter {
        fun loadMapData(mapId: Int, mapTitle: String)
        fun onNodeTapped(title: String, description: String)
    }
}