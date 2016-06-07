#sh script to update reportw service plugins (report-service, cgw-rule etc)
#to run this script, checkout cgw trunk - http://cvswiki1.wustl.edu/svn/ClinicalGenomicsWorkstation/trunk/software/
# run this script via  ./report-service-client/update_report_service_plugin_jar.sh

echo "===================================== Running ======================================="
echo "======================== Delete_all_build_folders.sh ========================"

cd ..

cd cgw-common
echo "Removing from: cgw_common"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw-syntax-generation
echo "Removing from: cgw-syntax-generation"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw-rule
echo "Removing from: cgw-rule"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw-grid
echo "Removing from: cgw-grid"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd report-service
echo "Removing from: report-service"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw-sample-plugin
echo "Removing from: cgw-sample-plugin"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw-cluster-manager
echo "Removing from: cgw-cluster-manager"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw-archiving
echo "Removing from: cgw-archiving"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..

cd cgw_sample
echo "Removing from: cgw-sample"
echo "Running : rm -rf build/ "
rm -rf build/
echo "---------"
cd ..
echo "REMVOED ALL BUILD FOLDERS"