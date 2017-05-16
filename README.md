# daris-bionics-client
Client applcations for Brain Bionics Projects. It includes a set of command line tools to upload data to Brain Bionics Projects in DaRIS. 

**NOTE:** The tools are designed and tested only for uploading data from the proposed **directory structure**:

* Project: &lt;EthicsOrg _Ethics#&gt;……………………………………………UOMHESC_1646801
  * Subject: &lt;pilot/Expt_initialsOfParticipants&gt;………Pilot_NP or Expt_NP
    * Session: &lt;YYYYMMMDD_Location&gt;………………………………………………2016May16_CfNE
      * Method: &lt;Instrument&gt;………………………………………………………………………EEG_Compumedics or EEG_gUSBamp
        * Data set:&lt;All files&gt;………………………………………………………………………*.hdf5,*.dat

**List of Tools**

**Name** | **Description** |
------------ | -------------
**bionics-dataset-upload** | Create/Upload a dataset from a local directory.  
**bionics-study-upload** | Create/Upload a study from a local directory hierarchy. Datasets will be created from the sub-directories. 
**bionics-subject-upload** | Create/Upload a subject from a local directory hierarchy.
**bionics-project-upload** | Upload subjects to the project from a local directory hierarchy.

## 1. Installation

* a. Install [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* b. Download [daris-bionics-client-0.0.3.zip](https://github.com/uom-daris/daris-bionics-client/releases/download/v0.0.3/daris-bionics-client-0.0.3.zip) and extract it:
  * **cd /opt/; sudo unzip daris-bionics-client-0.0.1.zip**
* c. Optionally, add the installed directory to PATH environment variable. On Linux/Mac, append the following line to **~/.bashrc**:
  * **export PATH=$PATH:/opt/daris-bionics-client-0.0.1**
* d. Configure the server and user authentication details in **~/.daris/daris-bionics-client.properties**:
  * **mf.host=mediaflux.yourdomain.org.au**
  * **mf.port=443**
  * **mf.transport=https**
  * **mf.auth=**
  * **mf.token=XXXXXXXXXXXXXXXXX**

**NOTE:** either **mf.auth** or **mf.token** need to be specified. **mf.auth** value is in the form of <domain,user,password> e.g.

  * **mf.auth=aaf:wilson:MyPASS**


## 2. Command Usage

### bionics-dataset-upload

* **Usage**
  * **bionics-dataset-upload --pid &lt;study-cid&gt; &lt;dataset-dir&gt;**

* **Example**
  * **bionics-dataset-upload --pid 1128.1.8.1.1.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE/EEG_Neuroscan**

### bionics-study-upload

* **Usage**
  * **bionics-study-upload --pid &lt;parent-cid&gt; &lt;study-dir&gt;**

* **Example**
  * **bionics-study-upload --pid 1128.1.8.1 ~/UOMHESC_1646801/Pilot_DUMMY/2017Jan16_CfNE**

### bionics-subject-upload

* **Usage**
  * **bionics-subject-upload --pid &lt;project-cid&gt; &lt;subject-dir&gt;**

* **Example**
  * **bionics-subject-upload --pid 1128.1.8 ~/UOMHESC_1646801/Pilot_DUMMY**

### bionics-project-upload

* **Usage**
  * **bionics-project-upload [--id &lt;project-cid&gt;] &lt;project-dir&gt;**

* **Example**
  * **bionics-project-upload --pid 1128.1.8 ~/UOMHESC_1646801**
  * **NOTE:** If --pid is not specified, it will look for the project with the same name as the project directory, e.g. UOMHESC_1646801, the command below will also work if the project name is **UOMHESC_1646801**
  * **bionics-subject-upload ~/UOMHESC_1646801**
  
  

