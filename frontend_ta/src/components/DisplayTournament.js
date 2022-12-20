import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';
import {getData} from "../utils.js"
import { GoToErrorPage } from '../utils.js';

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data:[],
      classes:[],
      badResponse: null
    };
   this.reloadPage=this.reloadPage.bind(this);
  }
  componentDidMount() {
    this.reloadPage();
    if(sessionStorage.getItem("username")!=null){
    getData("/api/classes/get-all").then((response) => {
      if (response.status === 200) {
        this.setState({classes: response.result,badResponse:null});
        console.log(response.result);}
       else this.setState({ badResponse: response.message });
      }
      );
    }
    }
  reloadPage(){
    getData("/api/tournament/list").then((response)=> {
      if (response.status === 200) {
       this.setState({data: response.result,badResponse:null});
       }
      else this.setState({ badResponse: response.message });
     }
     );
  }
  render() {
    if (this.state.badResponse!=null) return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <div class="main-panel">
        <h2>Tournaments</h2>
        <ListTournamentComponent tournaments={this.state.data} classes={this.state.classes} 
        backHome={this.props.backHome} index={this.props.index} reloadPage={this.reloadPage} />
      </div>
    );
  }

}