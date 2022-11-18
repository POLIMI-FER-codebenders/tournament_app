import "../styles/TournamentListComponent.css";
import React from 'react';
class ListTournamentComponent extends React.Component {
    constructor(props) {
      super(props);
      this.state = {
        tournaments: props.tournaments
      };
    }
  
    render() {
      return (
        <div class="list">
          <div class="list-headers flex-container">
            <div class="col1 flex-items">Name</div>
            <div class="col2 flex-items">Starting Date</div>
            <div class="col3 flex-items">Type</div>
            <div class="col4 flex-items"></div>
            <div class="col5 flex-items"></div>
          </div>
          {this.state.tournaments.map((object, i) => <Entry record={object} key={i} />)}
        </div>
      );
  
    }
  }
  
  function Entry(props){
    
      return (
        <TournamentEntry record={props.record}/>
      )
    }
    
    function JoinButton(status){
      if(status=="started"){
        return <div className="btn" onClick={console.log("Edition mode activate")}>Join</div>
      }
    
    }
    
  
  function TournamentEntry(props){
    return (
      <div class="list-entry flex-container">
        <div class="col1 flex-items">{props.record.name}</div>
        <div class="col2 flex-items">{props.record.date}</div>
        <div class="col2 flex-items">{props.record.status}</div>
        <div class="col3 flex-items">
          {JoinButton(props.record.status)}
        </div>
        <div class="col4 flex-items">
          <div class="btn" onClick={console.log("Edition mode activate")}>Live Score</div>
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
  
  export default ListTournamentComponent;