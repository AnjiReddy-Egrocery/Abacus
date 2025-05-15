package com.dst.abacustrainner.Model;

import java.util.List;

public class StudentTotalDetails {
    private String status;
    private String errorCode;
    private Result result;
    private String message;

    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }


    public static class Result{

        private String studentId;
        private String mId;
        private Object ufMid;
        private String joinDate;
        private String firstName;
        private String middleName;
        private String lastName;
        private String dateOfBirth;
        private String dobMonth;
        private String dobDate;
        private String gender;
        private String age;
        private Object emailId;
        private String motherTongue;
        private String profilePic;
        private String fatherName;
        private String fatherMobile;
        private String fatherQualification;
        private String fatherOccupation;
        private String motherName;
        private Object motherMobile;
        private String motherQualification;
        private String motherOccupation;
        private String parentEmail;
        private Object landLineNumber;
        private Object schoolName;
        private Object schoolMobile;
        private Object schoolAddress;
        private Object courseTypeId;
        private Object subCourseTypeId;
        private Object courseLevelId;
        private Object floatNo;
        private Object streetName;
        private Object areaName;
        private Object cityName;
        private Object pinCode;
        private Object stateId;
        private Object franchiseeCode;
        private Object studentCode;
        private Object startDate;
        private Object endDate;
        private Object classTime;
        private Object classDay;
        private Object courseInstructorName;
        private String registrationFee;
        private String levelFee;
        private String tax;
        private String kitFee;
        private String competitionFee;
        private Object userName;
        private String password;
        private String link;
        private String otp;
        private String isPwdSet;
        private String isOnline;
        private String isApproved;
        private String isPlatformStudent;
        private String academyId;
        private String gradeId;
        private Object sectionName;
        private String status;
        private Object batchName;
        private Object courseType;
        private Object subCourseType;
        private Object courseLevel;
        private Object instructorName;

        public Result(String sid, String fname, String laname, String profile) {

            this.studentId = sid;
            this.firstName = fname;
            this.lastName = laname;
            this.profilePic = profile;

        }


        public String getStudentId() {
            return studentId;
        }

        public void setStudentId(String studentId) {
            this.studentId = studentId;
        }

        public String getmId() {
            return mId;
        }

        public void setmId(String mId) {
            this.mId = mId;
        }

        public Object getUfMid() {
            return ufMid;
        }

        public void setUfMid(Object ufMid) {
            this.ufMid = ufMid;
        }

        public String getJoinDate() {
            return joinDate;
        }

        public void setJoinDate(String joinDate) {
            this.joinDate = joinDate;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getMiddleName() {
            return middleName;
        }

        public void setMiddleName(String middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getDobMonth() {
            return dobMonth;
        }

        public void setDobMonth(String dobMonth) {
            this.dobMonth = dobMonth;
        }

        public String getDobDate() {
            return dobDate;
        }

        public void setDobDate(String dobDate) {
            this.dobDate = dobDate;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getAge() {
            return age;
        }

        public void setAge(String age) {
            this.age = age;
        }

        public Object getEmailId() {
            return emailId;
        }

        public void setEmailId(Object emailId) {
            this.emailId = emailId;
        }

        public String getMotherTongue() {
            return motherTongue;
        }

        public void setMotherTongue(String motherTongue) {
            this.motherTongue = motherTongue;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getFatherName() {
            return fatherName;
        }

        public void setFatherName(String fatherName) {
            this.fatherName = fatherName;
        }

        public String getFatherMobile() {
            return fatherMobile;
        }

        public void setFatherMobile(String fatherMobile) {
            this.fatherMobile = fatherMobile;
        }

        public String getFatherQualification() {
            return fatherQualification;
        }

        public void setFatherQualification(String fatherQualification) {
            this.fatherQualification = fatherQualification;
        }

        public String getFatherOccupation() {
            return fatherOccupation;
        }

        public void setFatherOccupation(String fatherOccupation) {
            this.fatherOccupation = fatherOccupation;
        }

        public String getMotherName() {
            return motherName;
        }

        public void setMotherName(String motherName) {
            this.motherName = motherName;
        }

        public Object getMotherMobile() {
            return motherMobile;
        }

        public void setMotherMobile(Object motherMobile) {
            this.motherMobile = motherMobile;
        }

        public String getMotherQualification() {
            return motherQualification;
        }

        public void setMotherQualification(String motherQualification) {
            this.motherQualification = motherQualification;
        }

        public String getMotherOccupation() {
            return motherOccupation;
        }

        public void setMotherOccupation(String motherOccupation) {
            this.motherOccupation = motherOccupation;
        }

        public String getParentEmail() {
            return parentEmail;
        }

        public void setParentEmail(String parentEmail) {
            this.parentEmail = parentEmail;
        }

        public Object getLandLineNumber() {
            return landLineNumber;
        }

        public void setLandLineNumber(Object landLineNumber) {
            this.landLineNumber = landLineNumber;
        }

        public Object getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(Object schoolName) {
            this.schoolName = schoolName;
        }

        public Object getSchoolMobile() {
            return schoolMobile;
        }

        public void setSchoolMobile(Object schoolMobile) {
            this.schoolMobile = schoolMobile;
        }

        public Object getSchoolAddress() {
            return schoolAddress;
        }

        public void setSchoolAddress(Object schoolAddress) {
            this.schoolAddress = schoolAddress;
        }

        public Object getCourseTypeId() {
            return courseTypeId;
        }

        public void setCourseTypeId(Object courseTypeId) {
            this.courseTypeId = courseTypeId;
        }

        public Object getSubCourseTypeId() {
            return subCourseTypeId;
        }

        public void setSubCourseTypeId(Object subCourseTypeId) {
            this.subCourseTypeId = subCourseTypeId;
        }

        public Object getCourseLevelId() {
            return courseLevelId;
        }

        public void setCourseLevelId(Object courseLevelId) {
            this.courseLevelId = courseLevelId;
        }

        public Object getFloatNo() {
            return floatNo;
        }

        public void setFloatNo(Object floatNo) {
            this.floatNo = floatNo;
        }

        public Object getStreetName() {
            return streetName;
        }

        public void setStreetName(Object streetName) {
            this.streetName = streetName;
        }

        public Object getAreaName() {
            return areaName;
        }

        public void setAreaName(Object areaName) {
            this.areaName = areaName;
        }

        public Object getCityName() {
            return cityName;
        }

        public void setCityName(Object cityName) {
            this.cityName = cityName;
        }

        public Object getPinCode() {
            return pinCode;
        }

        public void setPinCode(Object pinCode) {
            this.pinCode = pinCode;
        }

        public Object getStateId() {
            return stateId;
        }

        public void setStateId(Object stateId) {
            this.stateId = stateId;
        }

        public Object getFranchiseeCode() {
            return franchiseeCode;
        }

        public void setFranchiseeCode(Object franchiseeCode) {
            this.franchiseeCode = franchiseeCode;
        }

        public Object getStudentCode() {
            return studentCode;
        }

        public void setStudentCode(Object studentCode) {
            this.studentCode = studentCode;
        }

        public Object getStartDate() {
            return startDate;
        }

        public void setStartDate(Object startDate) {
            this.startDate = startDate;
        }

        public Object getEndDate() {
            return endDate;
        }

        public void setEndDate(Object endDate) {
            this.endDate = endDate;
        }

        public Object getClassTime() {
            return classTime;
        }

        public void setClassTime(Object classTime) {
            this.classTime = classTime;
        }

        public Object getClassDay() {
            return classDay;
        }

        public void setClassDay(Object classDay) {
            this.classDay = classDay;
        }

        public Object getCourseInstructorName() {
            return courseInstructorName;
        }

        public void setCourseInstructorName(Object courseInstructorName) {
            this.courseInstructorName = courseInstructorName;
        }

        public String getRegistrationFee() {
            return registrationFee;
        }

        public void setRegistrationFee(String registrationFee) {
            this.registrationFee = registrationFee;
        }

        public String getLevelFee() {
            return levelFee;
        }

        public void setLevelFee(String levelFee) {
            this.levelFee = levelFee;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getKitFee() {
            return kitFee;
        }

        public void setKitFee(String kitFee) {
            this.kitFee = kitFee;
        }

        public String getCompetitionFee() {
            return competitionFee;
        }

        public void setCompetitionFee(String competitionFee) {
            this.competitionFee = competitionFee;
        }

        public Object getUserName() {
            return userName;
        }

        public void setUserName(Object userName) {
            this.userName = userName;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

        public String getIsPwdSet() {
            return isPwdSet;
        }

        public void setIsPwdSet(String isPwdSet) {
            this.isPwdSet = isPwdSet;
        }

        public String getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(String isOnline) {
            this.isOnline = isOnline;
        }

        public String getIsApproved() {
            return isApproved;
        }

        public void setIsApproved(String isApproved) {
            this.isApproved = isApproved;
        }

        public String getIsPlatformStudent() {
            return isPlatformStudent;
        }

        public void setIsPlatformStudent(String isPlatformStudent) {
            this.isPlatformStudent = isPlatformStudent;
        }

        public String getAcademyId() {
            return academyId;
        }

        public void setAcademyId(String academyId) {
            this.academyId = academyId;
        }

        public String getGradeId() {
            return gradeId;
        }

        public void setGradeId(String gradeId) {
            this.gradeId = gradeId;
        }

        public Object getSectionName() {
            return sectionName;
        }

        public void setSectionName(Object sectionName) {
            this.sectionName = sectionName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getBatchName() {
            return batchName;
        }

        public void setBatchName(Object batchName) {
            this.batchName = batchName;
        }

        public Object getCourseType() {
            return courseType;
        }

        public void setCourseType(Object courseType) {
            this.courseType = courseType;
        }

        public Object getSubCourseType() {
            return subCourseType;
        }

        public void setSubCourseType(Object subCourseType) {
            this.subCourseType = subCourseType;
        }

        public Object getCourseLevel() {
            return courseLevel;
        }

        public void setCourseLevel(Object courseLevel) {
            this.courseLevel = courseLevel;
        }

        public Object getInstructorName() {
            return instructorName;
        }

        public void setInstructorName(Object instructorName) {
            this.instructorName = instructorName;
        }



    }

}
