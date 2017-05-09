# daris-bionics-client
Client applcations for Brain Bionics Projects. It includes a set of command line tools to upload data to Brain Bionics Projects in DaRIS. 

**NOTE:** The tools are designed and tested only for uploading data from the proposed directory structure:

* **Directory Struture**
  * Project: < EthicsOrg _Ethics#>……………………………………………UOMHESC_1646801
    * Subject: <pilot/Expt_initialsOfParticipants>………Pilot_NP or Expt_NP
      * Session: <YYYYMMMDD_Location>………………………………………………2016May16_CfNE
        * Method: <Instrument>………………………………………………………………………EEG_Compumedics or EEG_gUSBamp
          * Data set:<All files>………………………………………………………………………*.hdf5,*.dat

**Table of Commands**

**Command** | **Description** |
------------ | -------------
**bionics-dataset-upload** | Create/Upload a dataset from a local directory.  
**bionics-study-upload** | Create/Upload a study from a local directory hierarchy. Datasets will be created from the sub-directories. 
**bionics-subject-upload** | Create/Upload a subject from a local directory hierarchy.
**bionics-project-upload** | Upload subjects to the project from a local directory hierarchy.

## 1. Installation

* a. Install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* b. Download [daris-bionics-client-x.x.x.zip]() and extract it:
  * **cd /opt/; sudo unzip daris-bionics-client-0.0.1.zip**
* c. Optionally, add the installed directory to PATH environment variable. On Unix, append the following line to ~/.bashrc:
  * **export PATH=$PATH:/opt/daris-bionics-client-0.0.1**
* d. Configure the server and user authentication details in ~/.daris/daris-bionics-client.properties:
  * **mf.host=mediaflux.yourdomain.org.au**
  * **mf.port=443**
  * **mf.transport=https**
  * **mf.auth=**
  * **mf.token=XXXXXXXXXXXXXXXXX**


## 2. Command Usage

### bionics-dataset-upload

* **Usage**
  * **bionics-dataset-upload --pid <study-cid> <dataset-dir>**

* **Example**
  * **bionics-dataset-upload --pid 1128.1.8.1.1.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE/EEG_Neuroscan**

### bionics-study-upload

* **Usage**
  * **bionics-study-upload --pid <parent-cid> <study-dir>**

* **Example**
  * **bionics-study-upload --pid 1128.1.8.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE**

### bionics-subject-upload

* **Usage**
  * **bionics-subject-upload [mediaflux-arguments] --pid <project-cid> <subject-dir>**

* **Example**
  * **bionics-study-upload --pid 1128.1.8.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE**
  

