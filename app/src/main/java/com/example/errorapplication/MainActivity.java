private void simulateNullPointerException() {
    try {
        String nullStr = null;
        // Fix: Check for null before calling length()
        if (nullStr != null) {
            nullStr.length();
        } else {
            throw new NullPointerException("String is null");
        }
    } catch (NullPointerException e) {
        Log.e(TAG, getString(R.string.null_pointer_exception), e);
        writeErrorToFile(getString(R.string.null_pointer_exception), e);
    }
}