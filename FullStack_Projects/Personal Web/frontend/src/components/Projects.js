import React, { useState, useEffect } from "react";

const Projects = () => {
  const [projects, setProjects] = useState([]);

  useEffect(() => {
    fetch("/api/projects")  // Calls the ASP.NET Core API
      .then(response => response.json())
      .then(data => setProjects(data))  // Store API response in state
      .catch(error => console.error("Error fetching projects:", error));
  }, []);

  return (
    <div>
      <h2>My Projects</h2>
      <ul>
        {projects.map((project, index) => (
          <li key={index}>{project}</li>
        ))}
      </ul>
    </div>
  );
};

export default Projects;
