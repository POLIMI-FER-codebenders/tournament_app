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
      asc:true
    };
    this.refreshView = this.refreshView.bind(this);
    this.OrderByName=this.OrderByName.bind(this);

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

  render() {
    if (this.state.badResponse !== null) return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <div class="list">
        <div class="list-tour-header">
        <div class="list-headers flex-container">
       
          <div class="col1 flex-items namefilter" onClick={this.OrderByName}>Name</div>
          <div class="col2 flex-items">Starting Date</div>
          <div class="col3 flex-items">Type</div>
          <div class="col4 flex-items">Status</div>
          <div class="col5 flex-items">Team Size</div>
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