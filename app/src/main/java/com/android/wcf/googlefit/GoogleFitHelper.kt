package com.android.wcf.googlefit


class GoogleFitHelper {
    companion object {
        const val GOOGLE_FIT_SHARED_PREF_NAME = "GoogleFit"
        const val GOOGLE_FIT_APP_SELECTED = "googlefit_app_selected"
        const val GOOGLE_FIT_APP_LOGGED_IN = "googlefit_app_logged_in"
        const val GOOGLE_FIT_APP_INFO = "googlefit_app_info"
        const val GOOGLE_FIT_USER_DISPLAY_NAME = "googlefit_user_display_name"
        const val GOOGLE_FIT_USER_DISPLAY_EMAIL = "googlefit_user_display_email"
    }


/*
    private void readStepsCountForWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this)).readData(readRequest)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<Bucket> buckets = dataReadResponse.getBuckets();

                        int totalSteps = 0;

                        for (Bucket bucket : buckets) {

                            List<DataSet> dataSets = bucket.getDataSets();

                            for (DataSet dataSet : dataSets) {
//                                if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {


                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        int steps = dp.getValue(field).asInt();
                                        totalSteps += steps;
                                    }
                                }
                            }
//                            }
                        }
                        Log.i(TAG, "total steps for this week: " + totalSteps);
                        mStepsWeekly.setText("Total steps for week: " + totalSteps);
                    }
                })
                .addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        List<Bucket> buckets = ((DataReadResponse) task.getResult()).getBuckets();

                        int totalSteps = 0;

                        for (Bucket bucket : buckets) {

                            List<DataSet> dataSets = bucket.getDataSets();

                            for (DataSet dataSet : dataSets) {
//                                if (dataSet.getDataType() == DataType.TYPE_STEP_COUNT_DELTA) {


                                for (DataPoint dp : dataSet.getDataPoints()) {
                                    for (Field field : dp.getDataType().getFields()) {
                                        int steps = dp.getValue(field).asInt();
                                        totalSteps += steps;
                                    }
                                }
                            }
//                            }
                        }
                        Log.i(TAG, "total steps for this week: " + totalSteps);
                        mStepsWeekly.setText("Total steps for week: " + totalSteps);
                    }
                });
    }

 */
}