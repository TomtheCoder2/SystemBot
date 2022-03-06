bash -c "screen -d -r v7 -X stuff $'say [scarlet]Server restart in 10 Seconds! All progress will be saved.\n'"
sleep 10
bash -c "screen -d -r v7 -X stuff $'save 1\n'"
sleep 1
bash -c "screen -d -r v7 -X stuff $'^C\n'"
sleep 1
bash -c "screen -d -r v7 -X stuff $'sh autoRestart.sh\nstop\nload 1\n'"
