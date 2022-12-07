import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';
import {getData} from "../utils.js"

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data:[],
      classes:[]
    };
   this.reloadPage=this.reloadPage.bind(this);
  }
  componentDidMount() {
    this.reloadPage();
    getData("/api/classes/get-all").then((response) => {
      if (response.status === 200) {
        this.setState({classes: response.result});
        console.log(response.result);}
       else console.log("error");
      }
      );
    }
  reloadPage(){
    getData("/api/tournament/list").then((response)=> {
      if (response.status === 200) {
       this.setState({data: response.result});
       console.log(response.result);}
      else console.log("error");
     }
     );
  }
  render() {
    return (
      <div class="main-panel">
        <h2>Tournaments</h2>
        <ListTournamentComponent tournaments={this.state.data} classes={this.state.classes} backHome={this.props.backHome} index={this.props.index} reloadPage={this.reloadPage} />
      </div>
    );
  }

}