import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';

class App extends Component {
  state = {
    isLoading: true,
    meetups: []
  };

  componentDidMount() {
    this.callApi()
      .then(response => {
        this.setState({ meetups: response, isLoading: false })
      })
      .catch(error => console.log(error));
  }

  callApi = async () => {
    const response = await fetch('/api/meetups');
    const body = await response.json();
    if (response.status !== 200) {
      throw Error(body.message);
    }
    return body;
  };

  render() {
    const {meetups, isLoading} = this.state;

    if (isLoading) {
      return <p>Loading...</p>;
    }

    return (
      <div className="App">
        <header className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h1 className="App-title">Welcome to React</h1>
        </header>
        <div className="App-intro">
          <h2>Meetup List</h2>
          {meetups.map(meetup =>
            <div key={meetup.id}>
              {meetup.name}
            </div>
          )}
        </div>
      </div>
    );
  }
}

export default App;
