import React from 'react';
import '../styles/App.css';
import ListTournamentComponent from './ListTournamentComponent';
import { getData } from "../utils.js"
import { GoToErrorPage } from '../utils.js';

export class DisplayTournament extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      data: [],
      classes: [],
      badResponse: null,
      tourname: "",
      backupdata: []
    };
    this.reloadPage = this.reloadPage.bind(this);
    this.handleChange = this.handleChange.bind(this);
  }

  componentDidMount() {
    this.reloadPage();
    if (sessionStorage.getItem("username") != null) {
      getData("/api/classes/get-all").then((response) => {
        if (response.status === 200) {

          this.setState({ classes: response.result, badResponse: null });
        }
        else this.setState({ badResponse: response.message });
      }
      );
    }
  }
  handleChange(event) {

    let tourname = event.target.value;
    this.setState({ tourname: event.target.value });
    this.setState({ data: this.state.backupdata.filter(entry => entry.name.startsWith(tourname)) });
  }
  reloadPage() {
    getData("/api/tournament/list").then((response) => {
      if (response.status === 200) {
        this.backupdata = response.result;
        this.setState({ data: response.result, badResponse: null, backupdata: response.result });
      }
      else this.setState({ badResponse: response.message });
    }
    );
  }
  render() {
    if (this.state.badResponse != null) return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
    return (
      <div class="main-panel">
        <h2>Tournaments</h2>
        <div id="searchbartour">
          
            <label className="labelsearchtour" htmlFor='toursearchtextarea' >
              Search tournament by name
              <textarea className="textareasearchtour" id="toursearchtextarea" value={this.state.tourname} onChange={this.handleChange} />
            </label>

          

        </div>
        <ListTournamentComponent tournaments={this.state.data} classes={this.state.classes}
          backHome={this.props.backHome} index={this.props.index} reloadPage={this.reloadPage} />
      </div>
    );
  }
}