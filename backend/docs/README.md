# Install/Build/Run Instructions
Note: post-Docker is latest version!

## TO RUN (pre-Docker):
* Run ./gradlew clean build
* Open a terminal in the /backend folder: ./gradlew run
* Open a terminal in the /frontend folder: (on first ever run) npm install
* In the /frontend folder: (on first ever run) npm install react-router-dom
* In the /frontend terminal: npm run dev
* Both servers must be running for the software to work
* It is located at localhost:5173

## TO RUN (post-Docker):
* manually make a file named .env in your backend folder if it doesn't exist already (yes, the file can be empty).
* Make sure Docker is running in the background.
* Once you merge this new code into your branch, run docker compose up --build in your terminal.
* Docker will configure the container on its own and start running.
* The program is present at localhost:5173.
* For running in the future, all you need to do is run docker compose up in your terminal and ctrl-c (or docker compose down) to stop.
* If you made/merged changes to backend or frontend, use the docker compose up --build command instead.