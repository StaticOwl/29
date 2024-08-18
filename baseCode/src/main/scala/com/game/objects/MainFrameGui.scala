package com.game.objects

import scala.swing.event.{ButtonClicked, SelectionChanged}
import scala.swing._

class MainFrameGui(players: List[Player]) extends SimpleSwingApplication {
  private val playerPanels: Map[String, Panel] = players.map { player =>
    player.getName -> player.playerPanel
  }.toMap

  private val playerNames: Seq[String] = players.map(_.getName)

  private val playerDropdown = new ComboBox(playerNames)

  private val headerPanel: FlowPanel = new FlowPanel {
    contents += new Label("Select a Player to View:")
    contents += playerDropdown
  }
  private val cardPanel:FlowPanel = new FlowPanel{
    contents += playerPanels(playerNames.headOption.getOrElse(""))
    listenTo(playerDropdown.selection)
    reactions+={
      case SelectionChanged(_) =>
        contents.clear()
        contents += playerPanels(playerDropdown.selection.item)
        revalidate()
        repaint()
    }
  }

  override def top: Frame = new MainFrame {
    title = "Card Game 29"
    preferredSize = new Dimension(600, 400)

    contents = new BoxPanel(Orientation.Vertical) {
      contents += headerPanel
      contents += cardPanel
    }
  }
}
