import { useState } from "react";
import postData from "../utils";
import '../styles/tournamentCreation.css';
import '../styles/App.css';
import '../styles/forms.css';

export function CreateTournament() {
  const [name, setName] = useState("");
  const [type, setType] = useState("KNOCKOUT");
  const [size, setSize] = useState(1);
  const [numberofteams, setnumberofteams] = useState(2);
  const [start_date, setDate] = useState("");
  const [gametype, setgameType] = useState("MULTIPLAYER");
  const [creationMessage, setCreationMessage] = useState("")
  const [numberofteamstext, setnumberofteamstext] = useState("Must be a power of 2 between 2 and 16")
  const [uploadclassmessage, setuploadclassmessage] = useState(null)
  const handleChange = (event) => {

    let eventsource = event.target.getAttribute('name')
    if (eventsource === "size")
      setSize(event.target.value);
    else if (eventsource === "starting_date")
      setDate(event.target.value);
    else if (eventsource === "numberteams")
      setnumberofteams(event.target.value);
    else if (eventsource === "gametype") {
      setgameType(event.target.value);
    }
    else if (eventsource === "tourtype") {
      if (event.target.value === "KNOCKOUT") setnumberofteamstext("Must be a power of 2 between 2 and 16");
      else setnumberofteamstext("Must be between 2 and 8");
      setType(event.target.value);

    }
  };
  function powerOfTwo(x) {
    return (Math.log(x) / Math.log(2)) % 1 === 0;
  }
  const handleSubmit = (event) => {
    event.preventDefault();
    console.log(!(numberofteams >= 2 && numberofteams <= 16));
    let data = { name: name, numberOfTeams: numberofteams, teamSize: size, type: type, matchType: gametype }
    if (name.length > 255) setCreationMessage("tournament successfully created");
    else if (!(size >= 1 && size <= 16)) setCreationMessage("team sizes must be from 1 to 16");
    else if (type === "KNOCKOUT" && !(numberofteams >= 2 && numberofteams <= 16)) setCreationMessage("number of teams for knockout must be even  from 2 to 16 ");
    else if (type === "LEAGUE" && !(numberofteams >= 2 && numberofteams <= 8)) setCreationMessage("number of teams for league must be even from 2 to 8 ");
    else if (type === "KNOCKOUT" && !powerOfTwo(numberofteams)) setCreationMessage("number of teams for knockout must be a power of 2 from 2 to 16 ");
    else postData("/api/tournament/create", data).then((response) => {

      if (response.status === 200) setCreationMessage("tournament successfully created")
      else {
        setCreationMessage(response.message);
      }

    }

    )
  }
  const handleClassSubmit = (event) => {
    event.preventDefault();
    const inputFile = document.getElementById("file");
    const formData = new FormData();
    for (const file of inputFile.files) {
      formData.append("file", file);

    }


    fetch(process.env.REACT_APP_BACKEND_ADDRESS + "/api/classes/upload", {
      method: "post",
      body: formData,
      credentials: 'include'
    }).then((response) => {
      if (response.status === 200) {
        setuploadclassmessage(<p>class successfully uploaded</p>)
      }
      else {
        let errortext = response.text();
        errortext.then(result => setuploadclassmessage(<p>{result}</p>));
      }
    })
  };

  return (
    <div className="main-panel" id="maintourcreationdiv">
      <div className="creation-form">
        <h2>Tournament creation</h2>

        <form onSubmit={handleSubmit}>
          <div className="input-container">
            <label htmlFor="tourname" >Name</label>
            <input id="tourname"
              type="text"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div className="input-container">
            <label htmlFor="typeselector">Type of tournament</label>
            <select className="selector" name="tourtype" id="typeselector" value={type} onChange={handleChange}>
              <option value="KNOCKOUT">Knockout</option>
              <option value="LEAGUE">League</option>
            </select>
          </div>
          <div className="input-container">
            <label htmlFor="gametypeselector">Type of game</label>
            <select class="selector" name="gametype" id="gametypeselector" value={gametype} onChange={handleChange}>
              <option value="MELEE">Melee</option>
              <option value="MULTIPLAYER">Multiplayer</option>
            </select>
          </div>
          <div className="input-container">
            <label htmlFor="teamsize-selector">Size of teams (1-10)</label>
            <input
              type="number" name="size" id="teamsize-selector"
              value={size}
              onChange={handleChange}
            />
          </div>
          <div className="input-container">
            <label htmlFor="numberofteams-selector">Number of teams
            <div className="descInput" id="expPolicy">
            {numberofteamstext}
            </div>
            </label>
            <input
              type="number" name="numberteams" id="numberofteams-selector"
              value={numberofteams}
              onChange={handleChange}
            />

          </div>
          <div className="input-container">
            <label htmlFor="tourdate">Starting date</label>
            <input id="tourdate" name="starting_date" type="date"
              value={start_date}

              onChange={handleChange}
            />
          </div>
          <div className="button-container">
            <input type="submit" value="Create tournament" />
          </div>
        </form>

        <p className='error'>{creationMessage}</p>
      </div>
      <div id="uploadclasses">
        <h3>Upload classes </h3>
        <form id="formclassupload" encType="multipart/form-data" onSubmit={handleClassSubmit}>
          <div class="input-container">
            <label for="file" id="uploadclasslabel">Select classes to upload</label>
            <input id="file" type="file" multiple />
          </div>
          <div className="button-container uploadclassbutton">
            <input type="submit" value="Upload" />
          </div>
        </form>
        <p className='error'>{uploadclassmessage}</p>
      </div>
    </div>
  );
}