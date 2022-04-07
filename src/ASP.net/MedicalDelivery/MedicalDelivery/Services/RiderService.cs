using MedicalDelivery.Helpers;
using MedicalDelivery.Models;
using Microsoft.AspNetCore.Mvc;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
namespace MedicalDelivery.Services
{
    public interface IRiderService
    {
        Rider create(Rider rider);
        IEnumerable<Rider> getAll();
        Rider getByID(int ID);
        Rider update(Rider rider, int ID);
    }
    public class RiderService : IRiderService
    {
        private LibraryContext context;
        public RiderService(LibraryContext context)
        {
            this.context = context;
        }
        public Rider create(Rider rider)
        {
            if (string.IsNullOrWhiteSpace(rider.firstName) || string.IsNullOrWhiteSpace(rider.lastName))
                throw new AppException("Name of rider is required");
            if (context.riders.Any(x => x.firstName == rider.firstName && x.lastName == rider.lastName))
                throw new AppException("Name is already in use");
            context.riders.Add(rider);
            context.SaveChanges();
            return rider;
        }
        public IEnumerable<Rider> getAll()
        {
            return context.riders;
        }

        public Rider getByID(int ID)
        {
            var rider = context.riders.Find(ID);
            return rider;
        }
        public Rider update([FromBody]Rider rider, int ID)
        {
            var oldRider = context.riders.Find(ID);
            if (string.IsNullOrWhiteSpace(rider.firstName) || string.IsNullOrWhiteSpace(rider.lastName))
                throw new AppException("Name of rider is required");
            if (context.riders.Any(x => x.firstName == rider.firstName && x.lastName == rider.lastName) && (oldRider.firstName != rider.firstName) && (oldRider.lastName != rider.lastName))
                throw new AppException("Name is already in use");
            oldRider.firstName = rider.firstName;
            oldRider.lastName = rider.lastName;
            context.SaveChanges();
            return rider;
        }

    }
}
