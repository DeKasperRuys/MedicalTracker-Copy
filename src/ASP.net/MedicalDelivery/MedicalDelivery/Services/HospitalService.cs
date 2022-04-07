using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace MedicalDelivery.Services
{
    public interface IHospitalService
    {
        Hospital create(Hospital hospital);
        IEnumerable<Hospital> getAll();
        Hospital getByID(int ID);
        Hospital update(Hospital hospital, int ID);
    }
    public class HospitalService : IHospitalService
    {
        private LibraryContext context;
        public HospitalService(LibraryContext context)
        {
            this.context = context;
        }
        public Hospital create(Hospital hospital)
        {
            if(string.IsNullOrWhiteSpace(hospital.name))
                throw new AppException("Name of hospital is required");
            if(hospital.lat == 0 || hospital.lon == 0 )
                throw new AppException("Please enter a correct address");
            if(context.hospitals.Any(x => x.name == hospital.name))
                throw new AppException("Name is already in use");
            if (string.IsNullOrWhiteSpace(hospital.address))
                throw new AppException("Address can't be null");
            context.hospitals.Add(hospital);
            context.SaveChanges();
            return hospital;
        }

        public IEnumerable<Hospital> getAll()
        {
            return context.hospitals;
        }

        public Hospital getByID(int ID)
        {
            var hospital = context.hospitals.Find(ID);
            return hospital;
        }

        public Hospital update([FromBody]Hospital hospital, int ID)
        {
            var oldHospital = context.hospitals.Find(ID);
            if (string.IsNullOrWhiteSpace(hospital.name))
                throw new AppException("Name of hospital is required");
            if (hospital.lat == 0 || hospital.lon == 0)
                throw new AppException("Please enter a correct address");
            if (context.hospitals.Any(x => x.name == hospital.name) && oldHospital.name != hospital.name)
                throw new AppException("Name is already in use");
            if (string.IsNullOrWhiteSpace(hospital.address))
                throw new AppException("Address can't be null");
            oldHospital.name = hospital.name;
            oldHospital.lat = hospital.lat;
            oldHospital.lon = hospital.lon;
            oldHospital.address = hospital.address;
            context.SaveChanges();
            return hospital;
        }
    }
}
