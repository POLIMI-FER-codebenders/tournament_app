import { Component } from "react";
import "../styles/listComponent.css";

class ListPlayers extends Component {
  constructor(props) {
    super(props);
    this.state = {
      players: props.players,
      handleClick: props.handleClick,
      btn_name: props.btn_name
    };
  }

  render() {
    return (
      <div class="list">
        <div class="list-headers flex-container">
          <div class="col1 flex-items">Name</div>
          <div class="col2 flex-items">Score</div>
          <div class="col3 flex-items"></div>
        </div>
        {this.state.players.map((object, i) => 
          <PlayerEntry obj={object} key={i} handleClick={event => this.state.handleClick(event, i)} btn_name={this.state.btn_name}/>
        )}
      </div>
    );

  }
}

function PlayerEntry(props) {
  return (
    <div class="list-entry flex-container">
      <div class="col1 flex-items">{props.obj.name}</div>
      <div class="col2 flex-items">{props.obj.score}</div>
      <div class="col3 flex-items">
        <div class="btn btn-green" onClick={props.handleClick}>Send invitation</div>
      </div>
    </div>
  );
}


export default ListPlayers;