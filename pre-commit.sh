set -e
log() {
	echo "[$0] $@"
}
handle_failure() {
	log "FAILURE"
	exit 1
}
trap "handle_failure" ERR INT TERM

gradle clean test

log "SUCCESS!"
