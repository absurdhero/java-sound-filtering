# java-sound-filtering
An experiment in writing audio filters with the Java Sound API.

The OneTrackMixer class is the part that reads from the default system input device,
runs the data through any defined filters, and outputs to the default system out device.

The filters package has my simple Filter interface and a few filters.
They fit together nicely.
