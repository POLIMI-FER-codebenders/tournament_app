import { Component } from "react";
import "../styles/listComponent.css";

class ListComponent extends Component {
  constructor(props) {
    super(props);
    this.state = {
      teams: props.teams,
      edit: false
    };
  }

  render() {
    return (
      <div class="list">
        <div class="list-headers flex-container">
          <div class="col1 flex-items">Name</div>
          <div class="col2 flex-items">Date created</div>
          <div class="col3 flex-items">Score</div>
          <div class="col4 flex-items"></div>
          <div class="col5 flex-items"></div>
        </div>
        {this.state.teams.map((object, i) => <Entry obj={object} key={i} edit={this.state.edit}/>)}
      </div>
    );

  }
}

function Entry(props){
  if (!props.edit) {
    return (
      <TeamEntry obj={props.obj}/>
    )
  } else {
    return (
      <TeamDetailsEntry obj={props.obj}/>
    )
  }
}

function TeamEntry(props){
  return (
    <div class="list-entry flex-container">
      <div class="col1 flex-items">{props.obj.name}</div>
      <div class="col2 flex-items">{props.obj.date}</div>
      <div class="col3 flex-items">{props.obj.score}</div>
      <div class="col4 flex-items">
        <div class="btn" onClick={console.log("Edition mode activate")}>Edit</div>
      </div>
      <div class="col5 flex-items">
        <div class="btn">Leave</div>
      </div>
    </div>
  );
}

function TeamDetailsEntry(props){
  return (
    <div class="list-entry flex-container">
      <div class="col1 flex-items">{props.obj.name}</div>
      <div class="col2 flex-items">{props.obj.date}</div>
      <div class="col3 flex-items">{props.obj.score}</div>
      <div class="col4 flex-items">
        <div class="btn" onClick={console.log("Edition mode disactivate")}>Confirm</div>
      </div>
      <div class="col5 flex-items">
        <div class="btn" onClick={console.log("Edition mode disactivate")}>Cancel</div>
      </div>
    </div>
  );
}

export default ListComponent;