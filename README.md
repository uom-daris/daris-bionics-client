# daris-bionics-client
Client applcations for Brain Bionics Projects. It includes a set of command line tools to upload data to Brain Bionics Projects in DaRIS. **NOTE:* The tools are designed and tested only for uploading data from the proposed directory structure.

* **Directory Struture**
  * Project: < EthicsOrg _Ethics#>……………………………………………UOMHESC_1646801
    * Subject: <pilot/Expt_initialsOfParticipants>………Pilot_NP or Expt_NP
      * Session: <YYYYMMMDD_Location>………………………………………………2016May16_CfNE
        * Method: <Instrument>………………………………………………………………………EEG_Compumedics or EEG_gUSBamp
          * Data set:<All files>………………………………………………………………………*.hdf5,*.dat

## Table of Commands
**Command** | **Description** |
------------ | -------------
**bionics-dataset-upload** | Create/Upload a dataset from a local directory.  
**bionics-study-upload** | Create/Upload a study from a local directory hierarchy. Datasets will be created from the sub-directories. 
**bionics-subject-upload** | Create/Upload a subject from a local directory hierarchy.
**bionics-project-upload** | Upload subjects to the project from a local directory hierarchy.

## Command Usage

### bionics-dataset-upload

* **Usage**
  * **bionics-dataset-upload [mediaflux-arguments] --pid <study-cid> <dataset-dir>**

* **Example**
  * **bionics-dataset-upload --pid 1128.1.8.1.1.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE/EEG_Neuroscan**

### bionics-dataset-upload

* **Usage**
  * **bionics-dataset-upload [mediaflux-arguments] --pid <study-cid> <dataset-dir>**

* **Example**
  * **bionics-dataset-upload --pid 1128.1.8.1.1.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE/EEG_Neuroscan**

