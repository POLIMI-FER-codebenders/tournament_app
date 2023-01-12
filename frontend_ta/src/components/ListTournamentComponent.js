import "../styles/TournamentListComponent.css";
import React from 'react';
import { TournamentEntry } from "./TournamentEntry";
import { GoToErrorPage } from '../utils';
import {getData} from "../utils.js"
class ListTournamentComponent extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      currentview: null,
      playerteam: null,
      playertournaments: null,
      badResponse:null,
      asc:true,
      dateasc:true,
      typeasc:true
    };
    this.refreshView = this.refreshView.bind(this);
    this.OrderByName=this.OrderByName.bind(this);
    this.OrderByDate=this.OrderByDate.bind(this);
    this.OrderByType=this.OrderByType.bind(this);
    this.OrderBySize=this.OrderBySize.bind(this);

  }
  refreshView(viewindex) {
    this.setState({ currentview: viewindex })
  }
  componentDidMount() {
    if (sessionStorage.getItem("username") != null) {
         
        getData("/api/team/get-mine").then((response) => {
            if (response.status === 200) {
                this.setState({ playerteam: response.result });
                if (this.state.playerteam != null) {
                    getData("/api/tournament/personal").then((response) => {
                        if (response.status === 200) {
                            this.setState({ playertournaments: response.result });

                        }
                        else this.setState({ badResponse: response.message })
                    })
                }
            }
            else this.setState({ badResponse: response.message });
        })

    }
    else {
        this.setState({ playerteam: null });
        this.setState({ playertournaments: null });

    }
}
OrderByName(){
let asc=this.state.asc;
  this.props.tournaments.sort(function (a, b) {
    if (a.name < b.name) {
      if(asc)  return -1;
      else return 1;
    }
    if (a.name > b.name) {
      if((asc)) return 1;
      else return -1;
    }
    return 0;
  })
  this.setState({asc:!this.state.asc});
}
OrderByDate(){
let asc=this.state.dateasc;
this.props.tournaments.sort(function (a, b) {
  if (a.startDate < b.startDate) {
    if(asc)  return -1;
    else return 1;
  }
  if (a.startDate > b.startDate) {
    if((asc)) return 1;
    else return -1;
  }
  return 0;
})
this.setState({dateasc:!this.state.dateasc});

}
OrderByType(){
  let asc=this.state.typeasc;
  this.props.tournaments.sort(function (a, b) {
    if (a.type==="KNOCKOUT") {
      if(asc)  return -1;
      else return 1;
    }
    if (a.type==="LEAGUE") {
      if((asc)) return 1;
      else return -1;
    }
   
    return 0;
  })
  this.setState({typeasc:!this.state.typeasc});
  }
  OrderBySize(){
    let asc=this.state.sizeasc;
    this.props.tournaments.sort(function (a, b) {
      if (a.teamSize<b.teamSize) {
        if(asc)  return -1;
        else return 1;
      }
      if (a.teamSize>b.teamSize) {
        if((asc)) return 1;
        else return -1;
      }
     
      return 0;
    })
    this.setState({sizeasc:!this.state.sizeasc});
    }



  render() {
    
    if (this.state.badResponse !== null) return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <div class="list">
        <div class="list-tour-header">
        <div class="list-headers flex-container">
       
          <div class="col1 flex-items filter" onClick={this.OrderByName}>Name</div>
          <div class="col2 flex-items filter" onClick={this.OrderByDate}>Starting Date</div>
          <div class="col3 flex-items filter" onClick={this.OrderByType}>Type</div>
          <div class="col4 flex-items">Status</div>
          <div class="col5 flex-items filter" onClick={this.OrderBySize}>Team Size</div>
          <div class="col6 flex-items"></div>
        </div>
        </div>
        {this.props.tournaments.map((object, i) => <TournamentEntry record={object} key={i}
         viewindex={i} refreshView={this.refreshView} currentview={this.state.currentview}
          backHome={this.props.backHome} index={this.props.index} reloadPage={this.props.reloadPage} 
          classes={this.props.classes} playerteam={this.state.playerteam}
          playertournaments={this.state.playertournaments} />)}
      </div>
    );

  }
}

export default ListTournamentComponent;