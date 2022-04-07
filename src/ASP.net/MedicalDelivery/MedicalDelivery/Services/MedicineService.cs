using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Services
{
    public interface IMedicineService
    {
        Medicine create(Medicine medicine );
        IEnumerable<Medicine> getAll();
        Medicine getByID(int ID);
        Medicine update(Medicine medicine, int ID);
    }
    public class MedicineService : IMedicineService
    {
        private LibraryContext context;
        public MedicineService(LibraryContext context)
        {
            this.context = context;
        }
        public Medicine create(Medicine medicine)
        {
            if (string.IsNullOrWhiteSpace(medicine.name))
                throw new AppException("Name of medicine is required");
            if(medicine.maxTemp < medicine.minTemp)
                throw new AppException("Maximum Temperature can not be lower than the minimum");
            if (medicine.maxHumid < medicine.minHumid)
                throw new AppException("Maximum Humidity can not be lower than the minimum");
            if (medicine.minTemp > medicine.maxTemp)
                throw new AppException("Minimum Temperature can not be higher than the maximum");
            if(medicine.minHumid > medicine.maxHumid)
                throw new AppException("Minimum Humidity can not he higher than the maximum");
            context.medicines.Add(medicine);
            context.SaveChanges();
            return medicine;
        }

        public IEnumerable<Medicine> getAll()
        {
            return context.medicines;
        }

        public Medicine getByID(int ID)
        {
            var medicine = context.medicines.Find(ID);
            return medicine;
        }

        public Medicine update([FromBody]Medicine medicine, int ID)
        {
            var oldMedicine = context.medicines.Find(ID);
            if (string.IsNullOrWhiteSpace(medicine.name))
                throw new AppException("Name of medicine is required");
            if(context.medicines.Any(x => x.name == medicine.name) && oldMedicine.name != medicine.name)
                throw new AppException("Name of medicine is already in use");
            if (medicine.maxTemp < medicine.minTemp)
                throw new AppException("Maximum Temperature can not be lower than the minimum");
            if (medicine.maxHumid < medicine.minHumid)
                throw new AppException("Maximum Humidity can not be lower than the minimum");
            if (medicine.minTemp > medicine.maxTemp)
                throw new AppException("Minimum Temperature can not be higher than the maximum");
            if (medicine.minHumid > medicine.maxHumid)
                throw new AppException("Minimum Humidity can not he higher than the maximum");
            //TODO Checkers
            oldMedicine.name = medicine.name;
            oldMedicine.maxHumid = medicine.maxHumid;
            oldMedicine.minHumid = medicine.minHumid;
            oldMedicine.maxTemp = medicine.maxTemp;
            oldMedicine.minTemp = medicine.minTemp;
            oldMedicine.orientation = medicine.orientation;
            oldMedicine.movement = medicine.movement;
            context.SaveChanges();
            return medicine;
        }
    }
}
