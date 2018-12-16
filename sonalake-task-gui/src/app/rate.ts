export class Rate {
    netPay: number;
    countryCode: string;
    currencyCode: string;

    constructor( country: string,  currency: string) {
        this.countryCode = country;
        this.currencyCode = currency;
    }
}
