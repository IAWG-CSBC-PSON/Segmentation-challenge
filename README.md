# Segmentation-challenge

### Getting started

A description of the overall challenge and the datasets can be found [here](https://docs.google.com/presentation/d/1Juoju0hh-ZxSVdx6FK-DjJGjZUksOBg3uPiQH-tBfEM/edit#slide=id.g70e21f1d70_0_89).  

Guidance on the expected format of the segmentation output can be found in the following file: 
[Guidance on deliverables.docx](https://github.com/IAWG-CSBC-PSON/Segmentation-challenge/files/6778338/Guidance.on.deliverables.docx)

We have set up some sample code using the [Allen Institute's Cell and Structure Segmenter](https://allencell.org/segmenter.html) framework.

The code is set up in Jupyter notebooks (readable in Jupyter Lab available within each instance during the hackathon).

#### To function properly you will need to correctly define `DATAPATH` 
Within each notebook, define `DATAPATH` as the top directory containing the example (or full) image datasets.
This currently works for an instance where the data were uploaded into the same directory as the notebook files. 
Please ensure you do NOT push any of the images to GitHub.

#### List of example code files

* [test_Harvard_Lung.ipynb](./test_code/test_Harvard_Lung.ipynb)
* [test_UNC_PCNA.ipynb](./test_code/test_UNC_PCNA.ipynb)
* [test_Vanderbilt_colon.ipynb](./test_code/test_Vanderbilt_colon.ipynb)
* [test_Vanderbilt_live_cell.ipynb](./test_code/test_Vanderbilt_live_cell.ipynb)


