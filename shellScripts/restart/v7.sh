bash -c "screen -d -r v7 -X stuff $'save 1\n'"
sleep 1
bash -c "screen -d -r v7 -X stuff $'^C\n'"
sleep 1
bash -c "screen -d -r v7 -X stuff $'java -jar server-release.jar\nstop\nload 1\n'"
